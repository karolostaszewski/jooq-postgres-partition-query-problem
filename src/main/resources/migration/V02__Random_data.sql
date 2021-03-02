do $$
begin
for r in 1..10000 loop
insert into partitioned_table(id, some_type, text_field) values(md5(random()::text), 'type_1', (1000*random())::text);
insert into partitioned_table(id, some_type, text_field) values(md5(random()::text), 'type_2', (1000*random())::text);
insert into partitioned_table(id, some_type, text_field) values(md5(random()::text), 'type_3', (1000*random())::text);
end loop;
end;
$$;