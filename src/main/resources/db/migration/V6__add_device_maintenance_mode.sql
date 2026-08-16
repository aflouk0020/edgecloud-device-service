ALTER TABLE edge_devices
    ADD COLUMN maintenance_mode BOOLEAN NOT NULL DEFAULT FALSE,
    ADD COLUMN maintenance_reason VARCHAR(500) NULL,
    ADD COLUMN maintenance_enabled_at DATETIME(6) NULL,
    ADD COLUMN maintenance_enabled_by BINARY(16) NULL,
    ADD COLUMN maintenance_scheduled_end_at DATETIME(6) NULL,
    ADD COLUMN maintenance_disabled_at DATETIME(6) NULL,
    ADD COLUMN maintenance_disabled_by BINARY(16) NULL;

CREATE TABLE device_maintenance_history (
    id BIGINT NOT NULL AUTO_INCREMENT,
    device_id BINARY(16) NOT NULL,
    action VARCHAR(20) NOT NULL,
    occurred_at DATETIME(6) NOT NULL,
    actor_user_id BINARY(16) NULL,
    reason VARCHAR(500) NULL,
    scheduled_end_at DATETIME(6) NULL,
    PRIMARY KEY (id)
);

CREATE INDEX idx_device_maintenance_expiry
    ON edge_devices (maintenance_mode, maintenance_scheduled_end_at);
CREATE INDEX idx_device_maintenance_history_device_time
    ON device_maintenance_history (device_id, occurred_at DESC, id DESC);
