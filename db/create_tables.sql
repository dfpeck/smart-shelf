CREATE TABLE ItemTypes (
    itemtypeid IDENTITY,
    itemtypename VARCHAR UNIQUE NOT NULL,
    iscontainer BOOLEAN NOT NULL DEFAULT FALSE,
    itemtypecomment VARCHAR,
    PRIMARY KEY (itemtypeid),
    );
CREATE TABLE Items (
    itemid IDENTITY,
    itemtype BIGINT NOT NULL,
    PRIMARY KEY (itemid),
    FOREIGN KEY (itemtype) REFERENCES ItemTypes(itemtypeid)
    );
CREATE TABLE MatTypes (
    mattypeid VARCHAR NOT NULL AUTO_INCREMENT,
    PRIMARY KEY (mattypeid)
    );
CREATE TABLE Mats (
    matid IDENTITY,
    mattype VARCHAR NOT NULL,
    matcomment VARCHAR,
    PRIMARY KEY (matid),
    FOREIGN KEY (mattype) REFERENCES MatTypes(mattypeid)
    );
CREATE TABLE EventTypes (
    eventtypeid BIGINT NOT NULL,    -- eventtypeid is not set automatically to
    eventtypename VARCHAR NOT NULL, --   ensure that IDs are consistent across
    PRIMARY KEY (eventtypeid)       --   platforms/implementations
    );
CREATE TABLE History (
    item BIGINT NOT NULL,
    datetime TIMESTAMP NOT NULL,
    mat BIGINT NOT NULL,
    eventtype BIGINT NOT NULL,
    sensors ARRAY,
    x REAL,
    y REAL,
    CONSTRAINT eventinfo PRIMARY KEY (item, datetime),
    FOREIGN KEY (item) REFERENCES Items(itemid),
    FOREIGN KEY (mat) REFERENCES Mats(matid),
    FOREIGN KEY (eventtype) REFERENCES EventTypes(eventtypeid)
    );

-----------------------
-- EVENTTYPE ENTRIES --
-----------------------
-- brand new item added to a mat
INSERT INTO EventTypes (eventtypeid, eventtypename) VALUES (0, 'ADDED');

-- item completely removed from a mat
INSERT INTO EventTypes (eventtypeid, eventtypename) VALUES (1, 'REMOVED');

-- item that was removed from a mat placed on a mat (may be the same mat or a
-- different mat
INSERT INTO EventTypes (eventtypeid, eventtypename) VALUES (2, 'REPLACED');

-- container reduced in weight
INSERT INTO EventTypes (eventtypeid, eventtypename) VALUES (3, 'REDUCED');

-- container increased in weight
INSERT INTO EventTypes (eventtypeid, eventtypename) VALUES (4, 'REFILLED');

-- item slid across a mat (NOT IMPLEMENTED)
--INSERT INTO EventTypes (eventname) VALUES ('SLID');
