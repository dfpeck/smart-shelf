CREATE TABLE ItemTypes (
    itypeid INTEGER NOT NULL AUTO_INCREMENT, 
    itypename VARCHAR, 
    iscontainer BOOLEAN, 
    PRIMARY KEY (itypeid)
    );
CREATE TABLE EventTypes (
    eventid INTEGER NOT NULL AUTO_INCREMENT, 
    eventname VARCHAR NOT NULL, 
    PRIMARY KEY (eventid)
    );
CREATE TABLE Items (
    itemid INTEGER NOT NULL AUTO_INCREMENT, 
    itemtype INTEGER NOT NULL, 
    PRIMARY KEY (itemid), 
    FOREIGN KEY (itemtype) REFERENCES ItemTypes(itypeid)
    );
CREATE TABLE Mats (
    matid INTEGER NOT NULL AUTO_INCREMENT,
    PRIMARY KEY (matid)
    );
CREATE TABLE History (
    item INTEGER NOT NULL, 
    datetime TIMESTAMP NOT NULL, 
    mat INTEGER NOT NULL, 
    event INTEGER NOT NULL, 
    sensor1 REAL, 
    sensor2 REAL, 
    sensor3 REAL, 
    sensor4 REAL, 
    x REAL NOT null, 
    y REAL NOT null, 
    CONSTRAINT eventinfo PRIMARY KEY (item, datetime), 
    FOREIGN KEY (item) REFERENCES Items(itemid), 
    FOREIGN KEY (event) REFERENCES EventTypes(eventid),
    FOREIGN KEY (mat) REFERENCES Mats(matid)
    );
INSERT INTO EventTypes (eventname) VALUES ('ADDED');
INSERT INTO EventTypes (eventname) VALUES ('REMOVED');
INSERT INTO EventTypes (eventname) VALUES ('REPLACED');
INSERT INTO EventTypes (eventname) VALUES ('REDUCED');
INSERT INTO EventTypes (eventname) VALUES ('REFILLED');
INSERT INTO EventTypes (eventname) VALUES ('SLID');
