CREATE TYPE some_type as ENUM ('type_1', 'type_2', 'type_3');

CREATE TABLE partitioned_table (
	id TEXT NOT NULL,
	some_type some_type NOT NULL,
	text_field TEXT NOT NULL,
	CONSTRAINT partitioned_table_pkey PRIMARY KEY (some_type, id)
)
PARTITION BY LIST (some_type);

CREATE TABLE partitioned_table_type_1_type2 PARTITION OF partitioned_table
FOR VALUES IN ('type_1', 'type_2');

CREATE TABLE partitioned_table_type_3 PARTITION OF partitioned_table
FOR VALUES IN ('type_3');