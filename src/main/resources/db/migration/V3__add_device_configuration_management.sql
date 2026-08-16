CREATE TABLE device_configurations (
    device_id BINARY(16) NOT NULL,
    polling_interval_seconds INT NOT NULL,
    heartbeat_interval_seconds INT NOT NULL,
    metrics_collection_interval_seconds INT NOT NULL,
    environment VARCHAR(20) NOT NULL,
    api_endpoint VARCHAR(500) NULL,
    logging_level VARCHAR(10) NOT NULL,
    tags_json VARCHAR(2000) NOT NULL,
    version BIGINT NOT NULL,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,
    updated_by BINARY(16) NOT NULL,
    PRIMARY KEY (device_id)
);

CREATE TABLE device_configuration_versions (
    id BIGINT NOT NULL AUTO_INCREMENT,
    device_id BINARY(16) NOT NULL,
    version BIGINT NOT NULL,
    polling_interval_seconds INT NOT NULL,
    heartbeat_interval_seconds INT NOT NULL,
    metrics_collection_interval_seconds INT NOT NULL,
    environment VARCHAR(20) NOT NULL,
    api_endpoint VARCHAR(500) NULL,
    logging_level VARCHAR(10) NOT NULL,
    tags_json VARCHAR(2000) NOT NULL,
    action VARCHAR(32) NOT NULL,
    changed_fields VARCHAR(1000) NOT NULL,
    changed_by BINARY(16) NOT NULL,
    changed_at DATETIME(6) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uk_device_configuration_version UNIQUE (device_id, version)
);
CREATE INDEX idx_device_configuration_history ON device_configuration_versions (device_id, version DESC);

CREATE TABLE device_configuration_templates (
    id BINARY(16) NOT NULL,
    name VARCHAR(100) NOT NULL,
    description VARCHAR(500) NULL,
    polling_interval_seconds INT NOT NULL,
    heartbeat_interval_seconds INT NOT NULL,
    metrics_collection_interval_seconds INT NOT NULL,
    environment VARCHAR(20) NOT NULL,
    api_endpoint VARCHAR(500) NULL,
    logging_level VARCHAR(10) NOT NULL,
    tags_json VARCHAR(2000) NOT NULL,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,
    created_by BINARY(16) NOT NULL,
    updated_by BINARY(16) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uk_device_configuration_template_name UNIQUE (name)
);
