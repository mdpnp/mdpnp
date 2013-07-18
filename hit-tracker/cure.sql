use hittracker;
create temporary table t1 as (select a.id, min(b.tm) as min_time
from
hits as a 
inner join hits as b on b.tm not between '2013-07-18 12:03:06' and '2013-07-18 12:03:08' and b.id >= a.id
where a.tm between '2013-07-18 12:03:06' and '2013-07-18 12:03:08'
group by a.id);

update hits, t1 set hits.tm = t1.min_time where hits.id = t1.id;
drop table t1;
