-- This SQL contains a "create table" that can be used to create a table that JdbcUsersConnectionRepository can persist
-- connection in. It is, however, not to be assumed to be production-ready, all-purpose SQL. It is merely representative
-- of the kind of table that JdbcUsersConnectionRepository works with. The table and column names, as well as the general
-- column types, are what is important. Specific column types and sizes that work may vary across database vendors and
-- the required sizes may vary across API providers.

create table user_connection (
    user_id varchar(255) not null,
	provider_id varchar(255) not null,
	provider_user_id varchar(255),
	rank int not null,
	display_name varchar(255),
	profile_url varchar(512),
	image_url varchar(512),
	access_token varchar(512) not null,
	secret varchar(512),
	refresh_token varchar(512),
	expire_time bigint,
	primary key (user_id, provider_id, provider_user_id));
create unique index user_connection_rank on user_connection(user_id, provider_id, rank);