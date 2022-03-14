SET 'auto.offset.reset'='earliest';

--Streams
SHOW ALL TOPICS;

CREATE STREAM tempReadings (zipcode VARCHAR, sensortime BIGINT, temp DOUBLE)
WITH (kafka_topic='readings', timestamp='sensortime', value_format='json', partitions=1);

SHOW TOPICS EXTENDED;
SHOW STREAMS EXTENDED;

INSERT INTO tempReadings (zipcode, sensortime, temp) VALUES ('25005', UNIX_TIMESTAMP(), 40);
INSERT INTO tempReadings (zipcode, sensortime, temp) VALUES ('25005', UNIX_TIMESTAMP(), 50);
INSERT INTO tempReadings (zipcode, sensortime, temp) VALUES ('25005', UNIX_TIMESTAMP() + 60*60*1000, 60);

CREATE STREAM tempReadings (zipcode VARCHAR, sensortime BIGINT, temp DOUBLE)
WITH (kafka_topic='readings', timestamp='sensortime', value_format='json', partitions=1);

SELECT zipcode, TIMESTAMPTOSTRING(WINDOWSTART, 'HH:mm:ss') AS windowtime, AVG(temp) as temp
FROM tempReadings
WINDOW TUMBLING (SIZE 1 HOURS)
GROUP BY zipcode EMIT CHANGES;

CREATE TABLE highsandlows WITH (kafka_topic='readings') AS
    SELECT zipcode, MIN(temp) as min_temp, MAX(temp) as max_temp
    FROM tempReadings
    GROUP BY zipcode;

--pull query
SELECT min_temp, max_temp, zipcode
FROM highsandlows
WHERE zipcode = '25005';

INSERT INTO tempReadings (zipcode, sensortime, temp) VALUES ('25005', UNIX_TIMESTAMP() + 60*60*1000, 70);

-- Will need to create another stream else we may create a loop and would get invalid topology issue.
CREATE STREAM tempReadings2 (zipcode VARCHAR, sensortime BIGINT, temp DOUBLE)
WITH (kafka_topic='readings2', timestamp='sensortime', value_format='json', partitions=1);

SELECT tempreadings2.temp, CASE
    WHEN tempreadings2.temp - highsandlows.min_temp <=5 THEN 'Low'
    WHEN highsandlows.max_temp - tempreadings2.temp <= 5 THEN 'High'
    ELSE 'Normal' END AS classification
FROM tempreadings2
LEFT JOIN highsandlows on tempreadings2.zipcode = highsandlows.zipcode
EMIT CHANGES;

INSERT INTO tempReadings2 (zipcode, sensortime, temp) VALUES ('25005', UNIX_TIMESTAMP(), 40);
INSERT INTO tempReadings2 (zipcode, sensortime, temp) VALUES ('25005', UNIX_TIMESTAMP(), 50);
INSERT INTO tempReadings2 (zipcode, sensortime, temp) VALUES ('25005', UNIX_TIMESTAMP() + 60*60*1000, 60);
