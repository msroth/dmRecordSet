/**
 * CollectionExamples - example uses of the dmRecordSet class.
 * (c) 2013 MS Roth - http://msroth.wordpress.com
 */

package com.dm_misc.collections.test;

import com.dm_misc.collections.dmRecordSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

import com.documentum.fc.client.*;
import com.documentum.fc.common.*;

public class dmRecordSet_examples {

    private static String DOCBASE = "repo1";
    private static String USERNAME = "dmadmin";
    private static String PASSWORD = "dmadmin";
    private static IDfClient client = null;
    private static IDfSessionManager sessionMgr = null;
    private static IDfSession session = null;
    private static String dql1 = "select r_object_id, object_name, r_creation_date, a_content_type, r_full_content_size, a_is_template from dm_document where folder('/Temp')";
    private static String dql2 = "select r_object_id, object_name, r_full_content_size from dm_document where title = 'qwerasdfzxcv'";
    private static String dql3 = "select r_object_id, object_name, r_full_content_size from dm_document where folder('/Temp')";

    /**
     * Example uses of the dmRecordSet class
     * @param args
     */
    public static void main(String[] args) {

        System.out.println("\n***** Examples using dmRecordSet *****");
        System.out.println(String.format("Class version: %s",dmRecordSet.getVersion()));

        try {

            client = new DfClient();
            sessionMgr = client.newSessionManager();
            IDfLoginInfo li = new DfLoginInfo();
            li.setUser(USERNAME);
            li.setPassword(PASSWORD);
            sessionMgr.setIdentity(DOCBASE, li);
            session = sessionMgr.getSession(DOCBASE);

            if (session == null) {
                System.out.println("Could not login.");
                System.exit(-1);
            }
            System.out.println(String.format("Logged in %s@%s", USERNAME, DOCBASE));

            System.out.println(String.format("\ntesting with query: %s", dql1));
            IDfCollection col = null;
            IDfTypedObject tObj = null;
            IDfQuery q = new DfQuery();
            q.setDQL(dql1);
            col = q.execute(session, DfQuery.DF_READ_QUERY);

            // ****************************************************************
            // get record set
            // ****************************************************************
            dmRecordSet dmRS = new dmRecordSet(col);
            System.out.println("Record count = " + dmRS.getRowCount());
            if (dmRS.isEmpty()) {
                System.out.println("dmRecordSet is empty");
            } else {
                System.out.println("dmRecordSet is NOT empty");
            }

            // ****************************************************************
            // get column names
            // ****************************************************************
            ArrayList<IDfAttr> cols = dmRS.getColumnDefs();
            for (IDfAttr a : cols) {
                System.out.print(a.getName() + "\t");
            }
            System.out.println();

            // ****************************************************************
            // process the record set
            // ****************************************************************
            while (dmRS.hasNext()) {

                tObj = dmRS.getNextRow();
                for (IDfAttr a : cols) {
                    System.out.print(tObj.getString(a.getName()) + "\t");
                }
                System.out.println();
            }

            // ****************************************************************
            // print first row
            // ****************************************************************
            System.out.println("\nMoved to beginning of record set");
            tObj = dmRS.getFirstRow();
            if (dmRS.isBOF()) {
            	for (IDfAttr a : cols) {
                    System.out.print(tObj.getString(a.getName()) + "\t");
                }
            	IDfSysObject sObj = (IDfSysObject) tObj.getSession().getObject(tObj.getId("r_object_id"));
            	sObj.checkout();
            	if (sObj.isCheckedOut()) System.out.println("checked out: " + sObj.getObjectName());
            	sObj.cancelCheckout();
                System.out.println(sObj.getTypeName());
            }

            // ****************************************************************
            // print last row
            // ****************************************************************
            System.out.println("\nMoved to end of record set");
            tObj = dmRS.getLastRow();
            if (dmRS.isEOF()) {
            	for (IDfAttr a : cols) {
                    System.out.print(tObj.getString(a.getName()) + "\t");
                }
                System.out.println();
            }

            // ****************************************************************
            // try to advance
            // ****************************************************************
            System.out.println("\nAdvance past end of record set");
            if (dmRS.hasNext()) {
                tObj = dmRS.getNextRow();
                for (IDfAttr a : cols) {
                    System.out.print(tObj.getString(a.getName()) + "\t");
                }
                System.out.println();
            } else {
                System.out.println("\tcould not advance, eof");
            }

            // ****************************************************************
            // do previous
            // ****************************************************************
            System.out.println("\nMove to previous row");
            if (dmRS.hasPrevious()) {
                tObj = dmRS.getPreviousRow();
                for (IDfAttr a : cols) {
                    System.out.print(tObj.getString(a.getName()) + "\t");
                }
                System.out.println();
            } else {
                System.out.println("\tcould not get previous, bof");
            }

            // ****************************************************************
            // print first row
            // ****************************************************************
            System.out.println("\nMoved to first row");
            tObj = dmRS.getFirstRow();
            if (dmRS.isBOF()) {
            	for (IDfAttr a : cols) {
                    System.out.print(tObj.getString(a.getName()) + "\t");
                }
                System.out.println();
            }

            // ****************************************************************
            // do previous
            // ****************************************************************
            System.out.println("\nMove to previous row");
            if (dmRS.hasPrevious()) {
                tObj = dmRS.getPreviousRow();
                for (IDfAttr a : cols) {
                    System.out.print(tObj.getString(a.getName()) + "\t");
                }
                System.out.println();
            } else {
                System.out.println("\tcould not get previous, bof");
            }

            // ****************************************************************
            // do random row
            // ****************************************************************
            Random generator = new Random();
            int r = generator.nextInt(dmRS.getRowCount());

            System.out.println(String.format("\nRandom row %d", r));
            tObj = dmRS.getRow(r);
            for (IDfAttr a : cols) {
                System.out.print(tObj.getString(a.getName()) + "\t");
            }
            System.out.println();

            // ****************************************************************
            // reset to beginning
            // ****************************************************************
            System.out.println("\nReset to beginning of record set");
            dmRS.resetToBeginning();
            System.out.println("First row");
            tObj = dmRS.getNextRow();
            for (IDfAttr a : cols) {
                System.out.print(tObj.getString(a.getName()) + "\t");
            }
            System.out.println();

            // ****************************************************************
            // do EOF row error
            // ****************************************************************
            r = dmRS.getRowCount() + 100;
            try {
                System.out.println(String.format("\nGet row %d (past EOF)",r));
                tObj = dmRS.getRow(r);
                for (IDfAttr a : cols) {
                    System.out.print(tObj.getString(a.getName()) + "\t");
                }
                System.out.println();
            } catch (Exception e) {
                System.out.println("\t" + e.getMessage());
            }

            // ****************************************************************
            // do BOF row error
            // ****************************************************************
            r = -100;
            try {
                System.out.println(String.format("\nGet row %d (past BOF)",r));
                tObj = dmRS.getRow(r);
                for (IDfAttr a : cols) {
                    System.out.print(tObj.getString(a.getName()) + "\t");
                }
                System.out.println();
            } catch (Exception e) {
                System.out.println("\t" + e.getMessage());
            }

            // ****************************************************************
            // process the record set backward
            // ****************************************************************
            System.out.println("\nProcessing record set backwards...");
            dmRS.resetToEnd();
            while (dmRS.hasPrevious()) {

                tObj = dmRS.getPreviousRow();
                for (IDfAttr a : cols) {
                    System.out.print(tObj.getString(a.getName()) + "\t");
                }
                System.out.println();
            }

            // ****************************************************************
            // add a row
            // ****************************************************************
            System.out.println("\nAdd a row to end of record set");
            System.out.println("Record count before = " + dmRS.getRowCount());
            dmRS.addRow(dmRS.getLastRow());
            System.out.println("Record count after = " + dmRS.getRowCount());

            // list all records
            dmRS.resetToBeginning();
            while (dmRS.hasNext()) {
            	tObj = dmRS.getNextRow();
            	for (IDfAttr a : cols) {
                    System.out.print(tObj.getString(a.getName()) + "\t");
                }
                System.out.println();
            }

            // ****************************************************************
            // add a bunch of rows
            // ****************************************************************
            if (dmRS.getRowCount() > 4)
            	r = 6;
            else r = dmRS.getRowCount();

            System.out.println(String.format("\nAdd %d rows to end of record set",r));
            ArrayList<IDfTypedObject> Temp = new ArrayList<IDfTypedObject>();

            for (int j = 0; j < r; j++) {
                Temp.add(dmRS.getRow(j));
            }

            System.out.println("Record count before = " + dmRS.getRowCount());
            dmRS.addRows(Temp);
            System.out.println("Record count after = " + dmRS.getRowCount());

            // list all records
            dmRS.resetToBeginning();
            while (dmRS.hasNext()) {
            	tObj = dmRS.getNextRow();
            	for (IDfAttr a : cols) {
                    System.out.print(tObj.getString(a.getName()) + "\t");
                }
                System.out.println();
            }

            // ****************************************************************
            // add an invalid rows to record set
            // ****************************************************************
            System.out.println("\nTry to add invalid rows to record set");
            System.out.println("using query: " + dql1);
            System.out.println("and query: " + dql3);
            
            q.setDQL(dql3);
            col = q.execute(session, DfQuery.DF_READ_QUERY);

            // get record set
            dmRecordSet dmRS3 = new dmRecordSet(col);
            try {
                dmRS.addRows(new ArrayList(dmRS3.getRecordSetAsList()));
            } catch (Exception e) {
                System.out.println(String.format("\tWARNING: %s",e.getMessage()));
            }
            
            // ****************************************************************
            // empty record set
            // ****************************************************************
            System.out.println("\nTest empty record set");
            System.out.println(String.format("testing with query: %s", dql2));
            col = null;
            tObj = null;
            q = new DfQuery();
            q.setDQL(dql2);
            col = q.execute(session, DfQuery.DF_READ_QUERY);

            // get record set
            dmRecordSet dmRS2 = new dmRecordSet(col);
            System.out.println("Record count = " + dmRS2.getRowCount());
            if (dmRS2.isEmpty()) {
                System.out.println("dmRecordSet is empty");
            } else {
                System.out.println("dmRecordSet is NOT empty");
            }
            
            // ****************************************************************
            // test null collection
            // ****************************************************************
            System.out.println("\nTest with null collection");
            try {
            	dmRS2 = new dmRecordSet(null);
            } catch (Exception e) {
            	System.out.println(String.format("\tWARNING: %s", e.getMessage()));
            }

            // ****************************************************************
            // get record set as list
            // ****************************************************************
            System.out.println("\nList of object IDs in record set");
            List<IDfTypedObject> list = dmRS.getRecordSetAsList();
            for (IDfTypedObject t : list) {
            	System.out.println(t.getString("r_object_id"));
            }

            // ****************************************************************
            // get record set as set
            // ****************************************************************
            System.out.println("\nSet of unique object IDs in record set");
            Set<IDfTypedObject> set = dmRS.getRecordSetAsSet();
            for (IDfTypedObject t : set) {
            	System.out.println(t.getString("r_object_id"));
            }

            // ****************************************************************
            // get record set stats
            // ****************************************************************
            System.out.println("\nFinal record set stats:");
            System.out.println(dmRS.getRecordSetInfo());
            
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            System.out.println("Done.");
            if (session != null && sessionMgr != null) {
                sessionMgr.release(session);
            }
        }
    }

}
// ****************************************************************
// <SDG><


