CREATE TABLE device_groups (
    id BINARY(16) NOT NULL,
    project_id BINARY(16) NOT NULL,
    name VARCHAR(100) NOT NULL,
    normalised_name VARCHAR(100) NOT NULL,
    description VARCHAR(500) NULL,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,
    created_by BINARY(16) NOT NULL,
    updated_by BINARY(16) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uk_device_group_project_name UNIQUE (project_id, normalised_name)
);
CREATE INDEX idx_device_groups_project_name ON device_groups (project_id, name);

CREATE TABLE device_group_memberships (
    group_id BINARY(16) NOT NULL,
    device_id BINARY(16) NOT NULL,
    assigned_at DATETIME(6) NOT NULL,
    assigned_by BINARY(16) NOT NULL,
    PRIMARY KEY (group_id, device_id),
    CONSTRAINT fk_group_membership_group FOREIGN KEY (group_id) REFERENCES device_groups (id) ON DELETE CASCADE,
    CONSTRAINT fk_group_membership_device FOREIGN KEY (device_id) REFERENCES edge_devices (id) ON DELETE CASCADE
);
CREATE INDEX idx_group_membership_device ON device_group_memberships (device_id, group_id);

CREATE TABLE device_tags (
    id BINARY(16) NOT NULL,
    project_id BINARY(16) NOT NULL,
    name VARCHAR(50) NOT NULL,
    normalised_name VARCHAR(50) NOT NULL,
    description VARCHAR(500) NULL,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,
    created_by BINARY(16) NOT NULL,
    updated_by BINARY(16) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uk_device_tag_project_name UNIQUE (project_id, normalised_name)
);
CREATE INDEX idx_device_tags_project_name ON device_tags (project_id, name);

CREATE TABLE device_tag_assignments (
    tag_id BINARY(16) NOT NULL,
    device_id BINARY(16) NOT NULL,
    assigned_at DATETIME(6) NOT NULL,
    assigned_by BINARY(16) NOT NULL,
    PRIMARY KEY (tag_id, device_id),
    CONSTRAINT fk_tag_assignment_tag FOREIGN KEY (tag_id) REFERENCES device_tags (id) ON DELETE CASCADE,
    CONSTRAINT fk_tag_assignment_device FOREIGN KEY (device_id) REFERENCES edge_devices (id) ON DELETE CASCADE
);
CREATE INDEX idx_tag_assignment_device ON device_tag_assignments (device_id, tag_id);
