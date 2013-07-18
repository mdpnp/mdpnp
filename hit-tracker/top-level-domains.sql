select distinct 
substring_index(remote_host,'.',-2) as tld
/*,
max(tm) as most_recent*/
,
count(*) as total_count
from hits where remote_host NOT RLIKE '^[[:digit:]]+\.[[:digit:]]+\.[[:digit:]]+\.[[:digit:]]+$'
group by tld
order by total_count desc



