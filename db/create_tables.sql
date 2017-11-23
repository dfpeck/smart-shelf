CREATE TABLE ItemTypes (
    itypeid INTEGER NOT NULL, 
    itypename STRING, 
    iscontainer INTEGER, 
    PRIMARY KEY (itypeid)
    );
CREATE TABLE EventTypes (
    eventid INTEGER NOT NULL, 
    eventname STRING NOT NULL, 
    PRIMARY KEY (eventid)
    );
CREATE TABLE Items (
    itemid INTEGER NOT NULL, 
    itemtype INTEGER NOT NULL, 
    PRIMARY KEY (itemid), 
    FOREIGN KEY (itemtype) REFERENCES ItemTypes(itypeid)
    );
CREATE TABLE Mats (
    matid INTEGER NOT NULL, 
    PRIMARY KEY (matid)
    );
CREATE TABLE History (
    item INTEGER NOT NULL, 
    time DATETIME NOT NULL, 
    mat INTEGER NOT NULL, 
    event INTEGER NOT NULL, 
    sensor1 REAL, 
    sensor2 REAL, 
    sensor3 REAL, 
    sensor4 REAL, 
    x REAL NOT NULL, 
    y REAL NOT NULL, 
    CONSTRAINT eventinfo PRIMARY KEY (item, time), 
    FOREIGN KEY (item) REFERENCES Items(itemid), 
    FOREIGN KEY (event) REFERENCES EventTypes(eventid)
    );
