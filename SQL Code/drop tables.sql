DROP TABLE PrintingMaterials cascade constraints;
DROP TABLE Uses cascade constraints;
DROP TABLE PrintingProfiles cascade constraints;
DROP TABLE PrintingJobs cascade constraints;
DROP TABLE artWorks cascade constraints;
DROP TABLE artWorks cascade constraints;
DROP TABLE Customers cascade constraints;
DROP TABLE Employees cascade constraints;
DROP TABLE TShirts cascade constraints;
DROP TABLE Orders cascade constraints;

DROP CLUSTER PrintPRO_printjOb_CLUSTER;

DROP VIEW customerorders;
DROP VIEW pendingorders;
DROP VIEW profileslist ;
DROP VIEW CustomerProductHistory;
DROP VIEW availabletshirts;
DROP VIEW overdueorders;

DROP SEQUENCE ordID_seq;

DROP TRIGGER orders_bi;

