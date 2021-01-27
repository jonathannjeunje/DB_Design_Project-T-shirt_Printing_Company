--What is the list of all pending, done, and caneled Orders passed by a Customer given the Customers email
CREATE VIEW customerorders 
AS 
  (SELECT orders.orddatetime, 
          orders.ordestdeliverydate, 
          orders.ordstatus, 
          orders.ordtotalcost, 
          printingjobs.proname, 
          printingjobs.tshbrand, 
          printingjobs.tshname, 
          printingjobs.tshcolor, 
          printingjobs.tshsize, 
          printingjobs.artname, 
          printingjobs.jobinstructions, 
          orders.cusemail 
   FROM   orders 
          INNER JOIN printingjobs 
                  ON ( orders.ordid = printingjobs.ordid )); 

--Provide the list of all overdue Orders. 
CREATE VIEW overdueorders 
AS 
  (SELECT empemail, 
          orddatetime, 
          ordestdeliverydate, 
          ordtotalcost 
   FROM   orders 
   WHERE  ordestdeliverydate < sysdate); 

--Provide the list of all the available TShirts. 
CREATE VIEW availabletshirts 
AS 
  (SELECT tshbrand, 
          tshname, 
          tshcolor, 
          tshsize, 
          tshamount, 
          tshprice 
   FROM   tshirts 
   WHERE  tshamount <> 0); 

--What are the printing profiles and T-Shirts ever used by a Customers given the Customers email? 
CREATE VIEW customerproducthistory 
AS 
  (SELECT orders.cusemail, 
          printingprofiles.proname, 
          printingprofiles.proprice, 
          tshirts.tshbrand, 
          tshirts.tshname, 
          tshirts.tshsize, 
          tshirts.tshcolor, 
          tshirts.tshprice 
   FROM   printingjobs 
          INNER JOIN printingprofiles 
                  ON printingjobs.proname = printingprofiles.proname 
          INNER JOIN tshirts 
                  ON ( printingjobs.tshbrand = tshirts.tshbrand 
                       AND printingjobs.tshname = tshirts.tshname 
                       AND printingjobs.tshcolor = tshirts.tshcolor 
                       AND printingjobs.tshsize = tshirts.tshsize ) 
          INNER JOIN orders 
                  ON ( orders.ordid = printingjobs.ordid )); 

--What are all the poending Orders with related printing jobs, Ordered by the estimated delivery date. 
CREATE VIEW pendingorders 
AS 
  (SELECT orders.ordid, 
          orders.ordestdeliverydate, 
          orders.ordtotalcost, 
          printingjobs.jobquantity, 
          printingjobs.jobinstructions, 
          printingjobs.proname, 
          printingjobs.tshbrand, 
          printingjobs.tshname, 
          printingjobs.tshsize, 
          printingjobs.tshcolor 
   FROM   orders 
          INNER JOIN printingjobs 
                  ON ( orders.ordid = printingjobs.ordid ) 
   WHERE  orders.ordstatus LIKE 'pending'); 

--Provide the list of all profiles. 
CREATE VIEW profileslist 
AS 
  (SELECT proname,
          promode, 
          prosize, 
          prodescription, 
          proprice 
   FROM   printingprofiles); 