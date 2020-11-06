SET search_path TO A2;

--If you define any views for a question (you are encouraged to), you must drop them
--after you have populated the answer table for that question.
--Good Luck!

--Query 1
INSERT INTO query1

--Query 2
INSERT INTO query2
(select tname, sum(capacity)
from tournament join court on tournament.tid = court.tid
group by tournament.tid
having sum(capacity) = (select max(seats) from
    (select tournament.tid, sum(capacity) as seats
    from tournament join court on tournament.tid = court.tid
    group by tournament.tid) tourneySeats)
order by tname asc);

--Query 3
INSERT INTO query3

--Query 4
INSERT INTO query4
(select distinct player.pid, pname 
from champion join player on champion.pid = player.pid
group by player.pid, pname
having count (distinct tid) in (select count(distinct tid) from tournament)
order by pname asc);

--Query 5
INSERT INTO query5

--Views (Query 6)
CREATE VIEW wins1 AS
(select player.pid as pid, pname, wins
from player join record on player.pid = record.pid
where year = 2011);

CREATE VIEW wins2 AS
(select player.pid as pid, pname, wins
from player join record on player.pid = record.pid
where year = 2012);

CREATE VIEW wins3 AS
(select player.pid as pid, pname, wins
from player join record on player.pid = record.pid
where year = 2013);

CREATE VIEW wins4 AS
(select player.pid as pid, pname, wins
from player join record on player.pid = record.pid
where year = 2014);

--Query 6
INSERT INTO query6
(select wins1.pid, wins1.pname
from wins1
join wins2 on wins1.pid = wins2.pid
join wins3 on wins1.pid = wins3.pid
join wins4 on wins1.pid = wins4.pid
where (wins4 > wins3) and (wins3 > wins2) and (wins2 > wins1)
order by pname asc);

--Drop Views (Query 6)
DROP VIEW wins1;
DROP VIEW wins2;
DROP VIEW wins3;
DROP VIEW wins4;

--Query 7
INSERT INTO query7

--Query 8
INSERT INTO query8
(select p1name, pname as p2name, cname
from(
	select pid, pname as p1name, cid as p1Country, opponentid 
	from(
		select winid as playerid, lossid as opponentid
		from event
		UNION
		select lossid, winid
		from event) matchups
	join player on pid = playerid) p1Info
join player on player.pid = opponentid
join country on country.cid = player.cid
where p1Country = player.cid
order by cname asc, p1name desc);

--Query 9
INSERT INTO query9

--Views (Query 10)
CREATE VIEW WinLossTable2014 AS
(select winner as pname, 
	wins.courtname as court, 
	wins.year as year, wins, losses
from(select pname as winner, courtname, year, count(*) as wins
	from event join player on winid = pid
	join court on court.courtid = event.courtid
	where year = 2014 group by pname, courtname, year) wins
left join(select pname as loser, courtname, year, count(*) as losses
	from event join player on lossid = pid
	join court on court.courtid = event.courtid
	where year = 2014 group by pname, courtname, year) losses
on losses.loser = wins.winner and losses.courtname = wins.courtname);

CREATE VIEW Played200MinutesAvg AS
(select pname, avg(duration)
from(select pname, duration
	from event join player on lossid = pid
	UNION ALL 
    select pname, duration
	from event join player on winid = pid) timeStats
group by pname
having avg(duration) > 200);

--Query 10
INSERT INTO query10
(select distinct pname
from WinLossTable2014
where pname not in 
	(select pname from WinLossTable2014 where losses >= wins)
and pname in 
	(select pname from Played200MinutesAvg)
order by pname desc);

--Drop views (Query 10)
DROP VIEW WinLossTable2014;
DROP VIEW Played200MinutesAvg;

