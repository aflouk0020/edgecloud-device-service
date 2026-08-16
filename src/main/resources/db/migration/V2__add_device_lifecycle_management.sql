ALTER TABLE edge_devices
    ADD COLUMN description VARCHAR(1000) NULL,
    ADD COLUMN physical_location VARCHAR(255) NULL,
    ADD COLUMN firmware_version VARCHAR(100) NULL,
    ADD COLUMN operating_system VARCHAR(100) NULL,
    ADD COLUMN active BOOLEAN NOT NULL DEFAULT TRUE,
    ADD COLUMN updated_at DATETIME(6) NULL;

UPDATE edge_devices SET updated_at = registered_at WHERE updated_at IS NULL;

ALTER TABLE edge_devices MODIFY updated_at DATETIME(6) NOT NULL;

CREATE INDEX idx_edge_devices_active ON edge_devices (active);
CREATE INDEX idx_edge_devices_status ON edge_devices (status);
CREATE INDEX idx_edge_devices_last_heartbeat ON edge_devices (last_heartbeat);
CREATE INDEX idx_edge_devices_registered_at ON edge_devices (registered_at);

CREATE TABLE device_lifecycle_history (
    id BIGINT NOT NULL AUTO_INCREMENT,
    device_id BINARY(16) NOT NULL,
    action VARCHAR(32) NOT NULL,
    occurred_at DATETIME(6) NOT NULL,
    actor_user_id BINARY(16) NOT NULL,
    device_name VARCHAR(255) NOT NULL,
    active BOOLEAN NOT NULL,
    details VARCHAR(1000) NULL,
    PRIMARY KEY (id)
);

CREATE INDEX idx_device_history_device_time
    ON device_lifecycle_history (device_id, occurred_at);
