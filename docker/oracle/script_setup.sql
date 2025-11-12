
ALTER SESSION SET CONTAINER = FREEPDB1;

begin
    execute immediate 'drop user developer cascade';
exception
    when others  then
        if sqlcode != -1918 then
            raise;
        end if;
end;

create user developer identified by developer123
    default tablespace users
    quota unlimited on users;

grant connect, resource to developer;

grant unlimited tablespace to developer;

commit;

