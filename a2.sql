SET search_path TO A2;

--If you define any views for a question (you are encouraged to), you must drop them
--after you have populated the answer table for that question.
--Good Luck!

--Query 1
INSERT INTO query1

--Query 2
INSERT INTO query2
(
select tname, sum(capacity) as totalCapacity
from tournament 
join court 
	on tournament.tid = court.tid
group by tournament.tid
having sum(capacity) =
	(select max(seats) from
		(select tournament.tid, sum(capacity) as seats
		from tournament 
		join court 
			on tournament.tid = court.tid
		group by tournament.tid) tourneySeats
		)
order by tname asc
);

--Query 3
INSERT INTO query3

--Query 4
INSERT INTO query4
(
select distinct player.pid, pname 
from champion
join player
	on champion.pid = player.pid
group by player.pid, pname
having count (distinct tid) in
	(select count(distinct tid) from tournament)
order by pname asc
);

--Query 5
INSERT INTO query5

--Views for Query 6
CREATE VIEW wins2011 AS
(
select player.pid as pid, pname, wins
from player 
join record 
	on player.pid = record.pid
where year = 2011
);
CREATE VIEW wins2012 AS
(
select player.pid as pid, pname, wins
from player 
join record 
	on player.pid = record.pid
where year = 2012
);
CREATE VIEW wins2013 AS
(
select player.pid as pid, pname, wins
from player 
join record 
	on player.pid = record.pid
where year = 2013
);
CREATE VIEW wins2014 AS
(
select player.pid as pid, pname, wins
from player 
join record 
	on player.pid = record.pid
where year = 2014
);

--Query 6
INSERT INTO query6
(
select
	wins2011.pid,
	wins2011.pname
from wins2011
join wins2012 on wins2011.pid = wins2012.pid
join wins2013 on wins2011.pid = wins2013.pid
join wins2014 on wins2011.pid = wins2014.pid
where
(wins2012 > wins2011) and (wins2013 > wins2012) and (wins2014 > wins2013)
order by pname asc
);

--Drop Views for Query 6
DROP VIEW wins2011;
DROP VIEW wins2012;
DROP VIEW wins2013;
DROP VIEW wins2014;

--Query 7
INSERT INTO query7

--Query 8
INSERT INTO query8
(
select p1name, pname as p2name, cname
from
(
	select pid, pname as p1name, cid as p1Country, opponentid 
	from
	(
		select winid as playerid, lossid as opponentid
		from event
		UNION
		select lossid, winid
		from event
	) matchups
	join player
		on pid = playerid
) p1Info
join player
	on player.pid = opponentid
join country
	on country.cid = player.cid
where p1Country = player.cid
order by cname asc, p1name desc
);

--Query 9
INSERT INTO query9

--Query 10
INSERT INTO query10

