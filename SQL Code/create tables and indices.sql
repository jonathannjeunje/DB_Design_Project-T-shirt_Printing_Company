
--Customer:
CREATE TABLE Customers(
	cusEmail VARCHAR2(100) NOT NULL,
	cusName VARCHAR2(50) NOT NULL,
	cusPhone# VARCHAR2(15),
	PRIMARY KEY (cusEmail)
);
--CREATE INDEX Customers_cusEmail_IDX ON Customers (cusemail);

--Employee:
CREATE TABLE Employees(
	empEmail VARCHAR2(100) NOT NULL,
	empName VARCHAR2(50) NOT NULL,
	empPhone# VARCHAR2(15),
	PRIMARY KEY (empEmail)
);
--CREATE INDEX Employees_empEmail_IDX ON Employees (empemail);


--Cluster File:
CREATE CLUSTER PrintPRO_printjOb_CLUSTER (proName VARCHAR2(50 BYTE));
--Printing Profile:
CREATE TABLE PrintingProfiles(
	proName VARCHAR2(50) NOT NULL,
	proMode VARCHAR2(50) NOT NULL,
	proSize VARCHAR2(10) NOT NULL,
	proPosition VARCHAR2(20) NOT NULL,
	proDescription VARCHAR2(100),
	proEstTime NUMBER(10,2) NOT NULL,
	proPrice NUMBER(10,2) NOT NULL,
    empEmail VARCHAR2(100) NOT NULL,
    CONSTRAINT chk_proMode_value CHECK (proMode IN ('blackandwhite', 'color')),
    CONSTRAINT chk_proSize_value CHECK (proSize IN ('small', 'medium', 'large')),
    CONSTRAINT chk_proPosition_value CHECK (proPosition IN ('front', 'back', 'Sleeves')),
    CONSTRAINT chk_proPrice_positive CHECK (proPrice >= 0),
	PRIMARY KEY (proName),
	FOREIGN KEY (empEmail) REFERENCES Employees(empEmail)
)
CLUSTER PrintPRO_printjOb_CLUSTER (proname);


--ArtWork:
CREATE TABLE ArtWorks(
	artName VARCHAR2(50) NOT NULL,
	artImage VARCHAR2(100) NOT NULL,
    cusEmail VARCHAR2(100) NOT NULL,
	PRIMARY KEY (artName),
	FOREIGN KEY (cusEmail) REFERENCES Customers(cusEmail)
);
--CREATE INDEX artwork_artname_IDX ON artwork (artname);

--Printing Material:
CREATE TABLE PrintingMaterials(
	matProduct# VARCHAR2(50) NOT NULL,
	matName VARCHAR2(50) NOT NULL,
	matAmount NUMBER(10,0) NOT NULL,
    CONSTRAINT chk_matAmount_positive CHECK (matAmount >= 0),
	PRIMARY KEY (matProduct#)
);
--CREATE INDEX PrintingMaterials_matProduct#_IDX ON PrintingMaterials (matProduct#);


--T-Shirt:
CREATE TABLE TShirts(
	tshBrand VARCHAR2(50) NOT NULL,
	tshName VARCHAR2(50) NOT NULL,
	tshColor VARCHAR2(50) NOT NULL,
	tshSize VARCHAR2(10) NOT NULL,	
	tshAmount NUMBER(10,0) NOT NULL,
	tshPrice NUMBER(10,2) NOT NULL,
    CONSTRAINT chk_tshSize_value CHECK (tshSize IN ('x-small', 'small', 'medium', 'large', 'x-large')),
    CONSTRAINT chk_tshAmount_positive CHECK (tshAmount >= 0),
	PRIMARY KEY (tshBrand, tshName, tshColor, tshSize)
)
ORGANIZATION INDEX;


--Order:
CREATE TABLE Orders(
    ordID INT NOT NULL,
	ordDateTime TIMESTAMP(2) DEFAULT LOCALTIMESTAMP(2) NOT NULL,
	ordTotalCost NUMBER(10,2),
	ordStatus VARCHAR2(20) DEFAULT 'pending' NOT NULL,
    ordEstDeliveryDate DATE,
    empEmail VARCHAR2(100) NOT NULL,
    cusEmail VARCHAR2(100) NOT NULL,
    CONSTRAINT chk_ordStatus_value CHECK (ordStatus in ('pending', 'done', 'delivered', 'cancelled', 'incomplete')),
    CONSTRAINT chk_ordCost_positive CHECK (ordTotalCost >= 0),
    CONSTRAINT chk_ordID_positive CHECK (ordID > 0),
	PRIMARY KEY (ordID),
	FOREIGN KEY (empEmail) REFERENCES Employees(empEmail),
	FOREIGN KEY (cusEmail) REFERENCES Customers(cusEmail)
)
ORGANIZATION INDEX;
CREATE INDEX orders_ordstatus_IDX ON orders (ordstatus);

--Use:
CREATE TABLE Uses(
	useProportion INT NOT NULL,
    proName VARCHAR2(50) NOT NULL,
   	matProduct# VARCHAR2(50) NOT NULL,
	PRIMARY KEY(proName, matProduct#),
    CONSTRAINT chk_useProportion_positive CHECK (useProportion >= 0),
	FOREIGN KEY (proName) REFERENCES PrintingProfiles(proName),
	FOREIGN KEY (matProduct#) REFERENCES PrintingMaterials(matProduct#)
);
--CREATE INDEX USEs_proNamematProduct#_IDX ON USES (proName, matProduct#);

--Printing Job:
CREATE TABLE PrintingJobs(
	jobQuantity INT DEFAULT 1 NOT NULL,
	jobUnitPrice NUMBER(10,2),
	jobTotalCost NUMBER(10,2),
	jobInstructions VARCHAR2(255),
	jobEstTime NUMBER(10,2) NOT NULL,
    proName VARCHAR2(50) NOT NULL,
    tshBrand VARCHAR2(50) NOT NULL,
	tshName VARCHAR2(50) NOT NULL,
	tshColor VARCHAR2(50) NOT NULL,
	tshSize VARCHAR2(10) NOT NULL,
    ordID INT NOT NULL,
    artName VARCHAR2(50) NOT NULL,
    CONSTRAINT chk_jobQuantity_positive CHECK (jobQuantity >= 0),
    CONSTRAINT chk_jobUnitPrice_positive CHECK (jobUnitPrice >= 0),
    CONSTRAINT chk_jobTotalCost_positive CHECK (jobTotalCost >= 0),
	PRIMARY KEY (proName, tshBrand, tshName, tshColor, tshSize, ordID),
	FOREIGN KEY (proName) REFERENCES PrintingProfiles(proName),
	FOREIGN KEY (tshBrand, tshName, tshColor, tshSize) REFERENCES TShirts(tshBrand, tshName, tshColor, tshSize),
	FOREIGN KEY (ordID) REFERENCES Orders(ordID),
	FOREIGN KEY (artName) REFERENCES ArtWorks(artName)
)
CLUSTER PrintPRO_printjOb_CLUSTER (proname);

CREATE INDEX PrintPRO_printjOb_IDX ON CLUSTER PrintPRO_printjOb_CLUSTER;

CREATE SEQUENCE ordID_seq START WITH 1;

CREATE TRIGGER orders_bi
BEFORE INSERT ON orders
FOR EACH ROW
BEGIN
  SELECT ordID_seq.nextval
  INTO :new.ordID
  FROM dual;
END;