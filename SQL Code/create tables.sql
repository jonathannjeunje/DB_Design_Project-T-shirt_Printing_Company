
--Customer:
CREATE TABLE Customers(
	cusEmail VARCHAR2(100) NOT NULL,
	cusName VARCHAR2(50) NOT NULL,
	cusPhone# VARCHAR2(15),
	PRIMARY KEY (cusEmail)
);

--Employee:
CREATE TABLE Employees(
	empEmail VARCHAR2(100) NOT NULL,
	empName VARCHAR2(50) NOT NULL,
	empPhone# VARCHAR2(15),
	PRIMARY KEY (empEmail)
);

--Printing Profile:
CREATE TABLE PrintingProfiles(
	proName VARCHAR2(50) NOT NULL,
	proMode VARCHAR2(50) NOT NULL,
	proSize NUMBER(2,1) NOT NULL,
	proPosition VARCHAR2(20) NOT NULL,
	proDescription VARCHAR2(100),
	proEstTime NUMBER(2,1) NOT NULL,
	proPrice NUMBER(4,2) NOT NULL,
    empEmail VARCHAR2(100) NOT NULL,
    CONSTRAINT chk_proMode_value CHECK (proMode IN ('blackandwhite', 'color')),
    CONSTRAINT chk_proSize_value CHECK (proSize IN ('small', 'medium', 'large')),
    CONSTRAINT chk_proPosition_value CHECK (proPosition IN ('front', 'back', 'Sleeves')),
    CONSTRAINT chk_proPrice_positive CHECK (proPrice >= 0),
	PRIMARY KEY (proName),
	FOREIGN KEY (empEmail) REFERENCES Employees(empEmail)
);

--ArtWork:
CREATE TABLE ArtWorks(
	artName VARCHAR2(50) NOT NULL,
	artImage VARCHAR2(100) NOT NULL,
    cusEmail VARCHAR2(100) NOT NULL,
	PRIMARY KEY (artName),
	FOREIGN KEY (cusEmail) REFERENCES Customers(cusEmail)
);

--Printing Material:
CREATE TABLE PrintingMaterials(
	matProduct# VARCHAR2(50) NOT NULL,
	matName VARCHAR2(50) NOT NULL,
	matAmount NUMBER(3,2) NOT NULL,
    CONSTRAINT chk_matAmount_positive CHECK (matAmount >= 0),
	PRIMARY KEY (matProduct#)
);

--T-Shirt:
CREATE TABLE TShirts(
	tshBrand VARCHAR2(50) NOT NULL,
	tshName VARCHAR2(50) NOT NULL,
	tshColor VARCHAR2(50) NOT NULL,
	tshSize VARCHAR2(10) NOT NULL,	
	tshAmount NUMBER(3,2) NOT NULL,
	tshPrice NUMBER(2,2) NOT NULL,
    CONSTRAINT chk_tshSize_value CHECK (tshSize IN ('x-small', 'small', 'medium', 'large', 'x-large')),
    CONSTRAINT chk_tshAmount_positive CHECK (tshAmount >= 0),
	PRIMARY KEY (tshBrand, tshName, tshColor, tshSize)
);

--Order:
CREATE TABLE Orders(
	ordDateTime TIMESTAMP(2) DEFAULT LOCALTIMESTAMP(2) NOT NULL,
	ordCost NUMBER(3,2),
	ordStatus VARCHAR2(20) DEFAULT 'pending' NOT NULL,
    ordEstDeliveryDate DATE,
    empEmail VARCHAR2(100) NOT NULL,
    cusEmail VARCHAR2(100) NOT NULL,
    CONSTRAINT chk_ordStatus_value CHECK (ordStatus in ('pending', 'done', 'delivered', 'cancelled')),
    CONSTRAINT chk_ordCost_positive CHECK (ordCost >= 0),
	PRIMARY KEY(ordDateTime, empEmail),
	FOREIGN KEY (empEmail) REFERENCES Employees(empEmail),
	FOREIGN KEY (cusEmail) REFERENCES Customers(cusEmail)
);

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

--Printing Job:
CREATE TABLE PrintingJobs(
	jobQuantity INT DEFAULT 1 NOT NULL,
	jobUnitPrice NUMBER(3,2),
	jobTotalCost NUMBER(4,2),
	jobInstructions VARCHAR2(255),
	jobEstTime NUMBER(4,1) NOT NULL,
    proName VARCHAR2(50) NOT NULL,
    tshBrand VARCHAR2(50) NOT NULL,
	tshName VARCHAR2(50) NOT NULL,
	tshColor VARCHAR2(50) NOT NULL,
	tshSize VARCHAR2(10) NOT NULL,
    ordDateTime TIMESTAMP(2) NOT NULL,
    empEmail VARCHAR2(100) NOT NULL,
    artName VARCHAR2(50) NOT NULL,
    CONSTRAINT chk_jobQuantity_positive CHECK (jobQuantity >= 0),
    CONSTRAINT chk_jobUnitPrice_positive CHECK (jobUnitPrice >= 0),
    CONSTRAINT chk_jobTotalCost_positive CHECK (jobTotalCost >= 0),
	PRIMARY KEY (proName, tshBrand, tshName, tshColor, tshSize, ordDateTime, empEmail),
	FOREIGN KEY (proName) REFERENCES PrintingProfiles(proName),
	FOREIGN KEY (tshBrand, tshName, tshColor, tshSize) REFERENCES TShirts(tshBrand, tshName, tshColor, tshSize),
	FOREIGN KEY (ordDateTime, empEmail) REFERENCES Orders(ordDateTime, empEmail),
	FOREIGN KEY (artName) REFERENCES ArtWorks(artName)
);