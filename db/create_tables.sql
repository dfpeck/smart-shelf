CREATE TABLE ItemTypes (
    itemtypeid IDENTITY,
    itemtypename VARCHAR UNIQUE NOT NULL,
    iscontainer BOOLEAN NOT NULL DEFAULT FALSE,
    itemtypecomment VARCHAR,
    PRIMARY KEY (itemtypeid)
    );
CREATE TABLE Items (
    itemid IDENTITY,
    itemtype BIGINT NOT NULL,
    PRIMARY KEY (itemid),
    FOREIGN KEY (itemtype) REFERENCES ItemTypes(itemtypeid)
    );
CREATE TABLE MatTypes (
    mattypeid VARCHAR,
    mattypecomment VARCHAR,
    PRIMARY KEY (mattypeid),
    CHECK (mattypeid != '')
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
