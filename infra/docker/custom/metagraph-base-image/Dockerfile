ARG TESSELLATION_VERSION_NAME

FROM metagraph-ubuntu-${TESSELLATION_VERSION_NAME}

ARG SHOULD_BUILD_GLOBAL_L0
ARG SHOULD_BUILD_DAG_L1
ARG SHOULD_BUILD_METAGRAPH_L0
ARG SHOULD_BUILD_CURRENCY_L1
ARG SHOULD_BUILD_DATA_L1
ARG TEMPLATE_NAME

ENV LC_ALL C.UTF-8
ENV LANG en_US.UTF-8
ENV LANGUAGE en_US.UTF-8

COPY project/$TEMPLATE_NAME $TEMPLATE_NAME
COPY global-l0/genesis/genesis.csv global-genesis.csv
COPY metagraph-l0/genesis/genesis.csv metagraph-genesis.csv

RUN mkdir shared_jars && mkdir shared_genesis

RUN apt-get update && \
    apt-get install -y \
    postgresql postgresql-contrib curl && \
    apt-get clean

RUN set -e; \
    if [ "$SHOULD_BUILD_GLOBAL_L0" = "true" ]; then \
        mkdir global-l0 && \
        cp global-l0.jar global-l0/global-l0.jar && \
        cp cl-wallet.jar global-l0/cl-wallet.jar && \
        cp cl-keytool.jar global-l0/cl-keytool.jar && \
        mv global-genesis.csv global-l0/genesis.csv; \
    fi

RUN set -e; \
    if [ "$SHOULD_BUILD_DAG_L1" = "true" ]; then \
        mkdir dag-l1 && \
        cp dag-l1.jar dag-l1/dag-l1.jar && \
        cp cl-wallet.jar dag-l1/cl-wallet.jar && \
        cp cl-keytool.jar dag-l1/cl-keytool.jar; \
    fi

RUN set -e; \
    if [ "$SHOULD_BUILD_METAGRAPH_L0" = "true" ]; then \
        mkdir metagraph-l0 && \
        cp cl-wallet.jar metagraph-l0/cl-wallet.jar && \
        cp cl-keytool.jar metagraph-l0/cl-keytool.jar && \
        rm -r -f $TEMPLATE_NAME/modules/l0/target && \
        cd $TEMPLATE_NAME && \
        sbt currencyL0/assembly && \
        cd .. && \
        mv $TEMPLATE_NAME/modules/l0/target/scala-2.13/*.jar metagraph-l0/metagraph-l0.jar && \
        mv metagraph-genesis.csv metagraph-l0/genesis.csv && \
        cp metagraph-l0/metagraph-l0.jar shared_jars/metagraph-l0.jar && \
        cp metagraph-l0/genesis.csv shared_genesis/genesis.csv; \
    fi

RUN set -e; \
    if [ "$SHOULD_BUILD_CURRENCY_L1" = "true" ]; then \
        mkdir currency-l1 && \
        cp cl-wallet.jar currency-l1/cl-wallet.jar && \
        cp cl-keytool.jar currency-l1/cl-keytool.jar && \
        rm -r -f $TEMPLATE_NAME/modules/l1/target && \
        cd $TEMPLATE_NAME && \
        sbt currencyL1/assembly && \
        cd .. && \
        mv $TEMPLATE_NAME/modules/l1/target/scala-2.13/*.jar currency-l1/currency-l1.jar && \
        cp currency-l1/currency-l1.jar shared_jars/currency-l1.jar; \
    fi

RUN set -e; \
    if [ "$SHOULD_BUILD_DATA_L1" = "true" ]; then \
        mkdir data-l1 && \
        cp cl-wallet.jar data-l1/cl-wallet.jar && \
        cp cl-keytool.jar data-l1/cl-keytool.jar && \
        rm -r -f $TEMPLATE_NAME/modules/data_l1/target && \
        cd $TEMPLATE_NAME && \
        sbt dataL1/assembly && \
        cd .. && \
        mv $TEMPLATE_NAME/modules/data_l1/target/scala-2.13/*.jar data-l1/data-l1.jar && \
        cp data-l1/data-l1.jar shared_jars/data-l1.jar; \
    fi

RUN rm -r -f cl-keytool.jar && \
    rm -r -f cl-wallet.jar && \
    rm -r -f global-l0.jar && \
    rm -r -f dag-l1.jar && \
    rm -r -f global-genesis.csv && \
    rm -r -f metagraph-genesis.csv && \
    rm -r -f tessellation && \
    rm -r -f $TEMPLATE_NAME

    # Environment variables for PostgreSQL
ENV POSTGRES_USER=social \
POSTGRES_PASSWORD=social \
POSTGRES_DB=social

# Expose PostgreSQL port
EXPOSE 5432

# Create the directory for init scripts and add the init.sql script
RUN mkdir -p /docker-entrypoint-initdb.d && \
    echo "CREATE TABLE calculated_states ( \
             ordinal BIGINT PRIMARY KEY, \
             state JSONB \
         );" > /docker-entrypoint-initdb.d/init.sql && \
    echo "GRANT ALL PRIVILEGES ON TABLE calculated_states TO $POSTGRES_USER;" >> /docker-entrypoint-initdb.d/init.sql

# Start PostgreSQL and create the necessary table
CMD service postgresql start && \
    su - postgres -c "psql -c \"CREATE USER $POSTGRES_USER WITH PASSWORD '$POSTGRES_PASSWORD';\"" && \
    su - postgres -c "psql -c \"CREATE DATABASE $POSTGRES_DB WITH OWNER $POSTGRES_USER;\"" && \
    su - postgres -c "psql $POSTGRES_DB -f /docker-entrypoint-initdb.d/init.sql" && \
    tail -f /dev/null

