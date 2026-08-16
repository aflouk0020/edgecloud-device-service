ALTER TABLE edge_devices
    ADD COLUMN last_recovery_at DATETIME(6) NULL,
    ADD COLUMN online_since DATETIME(6) NULL,
    ADD COLUMN heartbeat_state VARCHAR(20) NOT NULL DEFAULT 'UNKNOWN',
    ADD COLUMN consecutive_missed_heartbeats INT NOT NULL DEFAULT 0;

ALTER TABLE device_configurations ADD COLUMN heartbeat_timeout_seconds INT NOT NULL DEFAULT 90 AFTER heartbeat_interval_seconds;
ALTER TABLE device_configuration_versions ADD COLUMN heartbeat_timeout_seconds INT NOT NULL DEFAULT 90 AFTER heartbeat_interval_seconds;
ALTER TABLE device_configuration_templates ADD COLUMN heartbeat_timeout_seconds INT NOT NULL DEFAULT 90 AFTER heartbeat_interval_seconds;

CREATE TABLE device_heartbeat_history (
    id BIGINT NOT NULL AUTO_INCREMENT,
    device_id BINARY(16) NOT NULL,
    event_type VARCHAR(24) NOT NULL,
    heartbeat_at DATETIME(6) NOT NULL,
    previous_status VARCHAR(20) NOT NULL,
    resulting_status VARCHAR(20) NOT NULL,
    missed_heartbeats INT NOT NULL,
    recorded_at DATETIME(6) NOT NULL,
    PRIMARY KEY (id)
);

CREATE INDEX idx_heartbeat_history_device_time ON device_heartbeat_history (device_id, heartbeat_at DESC, id DESC);
CREATE INDEX idx_edge_devices_heartbeat_filter ON edge_devices (active, last_heartbeat);
