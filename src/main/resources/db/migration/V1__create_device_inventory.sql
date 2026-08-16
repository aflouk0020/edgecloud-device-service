CREATE TABLE edge_devices (
    id BINARY(16) NOT NULL,
    device_name VARCHAR(255) NOT NULL,
    device_type VARCHAR(255) NOT NULL,
    ip_address VARCHAR(255) NOT NULL,
    status VARCHAR(20) NOT NULL,
    registered_at DATETIME(6) NOT NULL,
    last_heartbeat DATETIME(6) NULL,
    PRIMARY KEY (id),
    CONSTRAINT uk_edge_devices_name UNIQUE (device_name)
);
