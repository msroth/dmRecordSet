/**
 * The dmRecordSet class provides a more functional alternative to the
 * IDfCollection for processing query results. <br/>
 *
 * (C) 2013 MS Roth
 * <p/>
 *
 * Example use: <br/>
 * <pre>
 *     IDfCollection col = null;
 *     String dql = "select r_object_id from dm_document where folder('/Temp')";
 *     IDfQuery q = new DfQuery();
 *     q.setDQL(dql);
 *     col = q.execute(session, DfQuery.DF_READ_QUERY);
 *
 *     // get record set
 *     dmRecordSet dmRS = new dmRecordSet(col);
 *
 *     System.out.println("Record count = " + dmRS.getRowCount());
 *
 *     if (dmRS.isEmpty())
 *         System.out.println("dmRecordSet is empty");
 *     else
 *         System.out.println("dmRecordSet is NOT empty");
 *
 * 		while (dmRS.hasNext()) {
 * 			try {
 *         		tObj = dmRS.getNextRow();
 *              for (IDfAttr a : dmRS.getColumnDefs()) {
 *                   System.out.print(tObj.getString(a.getName()) + "\t");
 *              }
 *              System.out.println();
 *         	} catch (Exception e) {
 *      		System.out.println("ERROR: " + e.getMessage());
 *      	}
 *      }
 * </pre>
 *
 * @author M. Scott Roth, http://msroth.wordpress.com
 * @version 1.2
 */
package com.dm_misc.collections;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.documentum.fc.client.IDfCollection;
import com.documentum.fc.client.IDfTypedObject;
import com.documentum.fc.common.DfLogger;
import com.documentum.fc.common.IDfAttr;

/**
 * The dmRecordSet class provides a more functional alternative to the
 * IDfCollection for processing query results.
 */
public class dmRecordSet {

    // private class variables
    private ArrayList<IDfAttr> _columnDefs = new ArrayList<IDfAttr>();
    private ArrayList<IDfTypedObject> _rows = new ArrayList<IDfTypedObject>();
    private IDfTypedObject _currentRow = null;
    private int _rowCount = 0;
    private int _currentRowNumber = -1;
    private int _firstRow = -1;
    private int _lastRow = -1;
    private boolean _bof = true;
    private boolean _eof = true;
    private String _colNamesHash = "";
    private static final String _version = "dmRecordSet v1.2, (c) 2013 MS Roth, http://msroth.wordpress.com";

    /**
     * Create a dmRecordSet from an IDfCollection.
     *
     * @param col The IDfCollection containing the query results
     * @exception Exception
     *
     */
    public dmRecordSet(IDfCollection col) throws Exception {

        DfLogger.info(dmRecordSet.class, _version, null, null);
        if (col != null && col.getState() != IDfCollection.DF_CLOSED_STATE) {

            /**
             * The record set is maintained in two internal arrays: _columnDefs,
             * _rows. _columnDefs contains IDfAttr objects for each column in
             * the collection (in order). _rows contains IDfTypedObjects for
             * each row in the collection. Using the two internal arrays
             * together you can get everything you need to know about the
             * collection.
             */
            // load column defs
            int c = col.getAttrCount();
            for (int i = 0; i < c; i++) {
                _columnDefs.add(col.getAttr(i));
                
                // create column name hash
                /**
                 * This is really just a concatenation of column names used
                 * to compare columns of IDfTypedObjects when trying to 
                 * manually add to the record set.
                 */
                _colNamesHash += col.getAttr(i).getName();
            }

            // load rows
            while (col.next()) {
                _rows.add(col.getTypedObject());
            }

            // init row counters
            _rowCount = _rows.size();

            if (_rowCount > 0) {
                _bof = true;
                _eof = false;
                _firstRow = 0;
                _lastRow = _rowCount - 1;
            } else {
                _bof = true;
                _eof = true;
                _firstRow = -1;
                _lastRow = -1;
            }
           
            // close collection
            col.close();

        } else {
            throw new Exception("The IDfCollection object is null or in the closed state.");
        }
    }

    /**
     * Return the number of rows contained in the record set.
     *
     * @return int row count
     *
     */
    public int getRowCount() {
        return _rowCount;
    }

    /**
     * Return the number of columns in record set
     *
     * @return int column count
     */
    public int getColumnCount() {
        return _columnDefs.size();
    }

    /**
     * Returns an ArrayList holding IDfAttr objects for each column definition.
     *
     * @return ArrayList<IDfAttr>
     *
     */
    public ArrayList<IDfAttr> getColumnDefs() {
        return _columnDefs;
    }

    /**
     * Beginning of file indicator.
     *
     * @return true, if current record pointer is at the beginning of the record
     * set, else false.
     *
     */
    public boolean isBOF() {
        return _bof;
    }

    /**
     * End of file indicator.
     *
     * @return true, if current record pointer is at the end of the record set,
     * else false.
     *
     */
    public boolean isEOF() {
        return _eof;
    }

    /**
     * Empty records set indicator.
     *
     * @return true, if record set contains no rows (i.e., it is empty), else
     * false.
     *
     */
    public boolean isEmpty() {
        if (_rowCount == 0) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Indicates if the record set has more rows.
     *
     * @return true, if record has more rows beyond current row, else false.
     *
     */
    public boolean hasNext() {
        if (_currentRowNumber + 1 <= _lastRow) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Get the next record in the record set. If no more rows exist beyond the
     * current row, return the current row and set EOF flag. This method propels
     * the reading of the record set forward.
     *
     * @return IDfTypedObject object representing the next row in the record
     * set.
     *
     * @see dmRecordSet#getNextRow() getNextRow
     *
     */
    @Deprecated
    public IDfTypedObject next() {
//        _currentRowNumber++;
//        if (_currentRowNumber < _lastRow) {
//            _currentRow = _rows.get(_currentRowNumber);
//            _bof = false;
//            _eof = false;
//        } else if (_currentRowNumber == _lastRow) {
//            _currentRow = _rows.get(_currentRowNumber);
//            _bof = false;
//            _eof = true;
//        } else {
//            _currentRowNumber = _lastRow;
//            _currentRow = _rows.get(_currentRowNumber);
//            _bof = false;
//            _eof = true;
//        }
//
//        return _currentRow;
        IDfTypedObject tObj;
        try {
            tObj = getRow(_currentRowNumber + 1);
        } catch (Exception e) {
            tObj = null;
        }
        return tObj;
    }

    /**
     * Get the next record in the record set. If no more rows exist beyond the
     * current row, return the current row and set EOF flag. This method propels
     * the reading of the record set forward.
     *
     * @return IDfTypedObject object representing the next row in the record
     * set.
     *
     */
    public IDfTypedObject getNextRow() throws Exception {
        return getRow(_currentRowNumber + 1);
    }

    /**
     * Indicates if the record set has a row preceding the current row.
     *
     * @return true, if current record has a row preceding the current row, else
     * false.
     *
     */
    public boolean hasPrevious() {
        if (_currentRowNumber - 1 >= _firstRow) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Get the previous record in the record set. If no more rows exist ahead of
     * the current row, return the current row and set BOF flag. Propels the
     * process backward.
     *
     * @return IDfTypedObject object representing the previous row in the record
     * set.
     *
     * @see dmRecordSet#getPreviousRow() getPreviousRow
     *
     */
    @Deprecated
    public IDfTypedObject previous() {
//        _currentRowNumber--;
//        if (_currentRowNumber > _firstRow) {
//            _currentRow = _rows.get(_currentRowNumber);
//            _bof = false;
//            _eof = false;
//        } else if (_currentRowNumber == _firstRow) {
//            _currentRow = _rows.get(_currentRowNumber);
//            _bof = true;
//            _eof = false;
//        } else {
//            _currentRowNumber = _firstRow;
//            _currentRow = _rows.get(_currentRowNumber);
//            _bof = true;
//            _eof = false;
//        }
//
//        return _currentRow;
        IDfTypedObject tObj;
        try {
            tObj = getRow(_currentRowNumber - 1);
        } catch (Exception e) {
            tObj = null;
        }
        return tObj;
    }

    /**
     * Get the previous record in the record set. If no more rows exist ahead of
     * the current row, return the current row and set BOF flag. Propels the
     * process backward.
     *
     * @return IDfTypedObject object representing the previous row in the record
     * set.
     *
     */
    public IDfTypedObject getPreviousRow() throws Exception {
        return getRow(_currentRowNumber - 1);
    }

    /**
     * Get the first record in the record set.
     *
     * @return IDfTypedObject object representing the first row in the record
     * set.
     *
     * @see dmRecordSet#getFirstRow() getFirstRow
     *
     */
    @Deprecated
    public IDfTypedObject first() {
//        _currentRowNumber = _firstRow;
//        _currentRow = _rows.get(_currentRowNumber);
//        _eof = false;
//        _bof = true;
//        return _currentRow;
        return getFirstRow();
    }

    /**
     * Get the first record in the record set.
     *
     * @return IDfTypedObject object representing the first row in the record
     * set.
     *
     */
    public IDfTypedObject getFirstRow() {
        _currentRowNumber = _firstRow;
        _currentRow = _rows.get(_currentRowNumber);
        _eof = false;
        _bof = true;
        return _currentRow;
    }

    /**
     * Get the last record in the record set.
     *
     * @return IDfTypedObject object representing the last row in the record
     * set.
     *
     * @see dmRecordSet#getLastRow() getLastRow
     *
     */
    @Deprecated
    public IDfTypedObject last() {
//        _currentRowNumber = _lastRow;
//        _currentRow = _rows.get(_currentRowNumber);
//        _eof = true;
//        _bof = false;
//        return _currentRow;
        return getLastRow();
    }

    /**
     * Get the last record in the record set.
     *
     * @return IDfTypedObject object representing the last row in the record
     * set.
     *
     */
    public IDfTypedObject getLastRow() {
        _currentRowNumber = _lastRow;
        _currentRow = _rows.get(_currentRowNumber);
        _eof = true;
        _bof = false;
        return _currentRow;
    }

    /**
     * Get any record in the record set.
     *
     * @param rowNumber record number to return
     * @return IDfTypedObject object representing the specified record.
     * @exception Exception if trying to access beyond BOF or EOF
     *
     */
    public IDfTypedObject getRow(int rowNumber) throws Exception {
        if ((rowNumber >= _firstRow) && (rowNumber <= _lastRow)) {
            _currentRowNumber = rowNumber;
            _currentRow = _rows.get(_currentRowNumber);
            _eof = false;
            _bof = false;
        } else if (rowNumber < _firstRow) {
            _currentRowNumber = _firstRow;
            _currentRow = _rows.get(_currentRowNumber);
            _eof = false;
            _bof = true;
            DfLogger.warn(dmRecordSet.class, String.format("Row %d precedes first row.  dmRecordSet reset to BOF.", rowNumber), null, null);
            throw new Exception(String.format("WARNING: Row %d precedes first row.  dmRecordSet reset to BOF.", rowNumber));
        } else if (rowNumber > _lastRow) {
            _currentRowNumber = _lastRow;
            _currentRow = _rows.get(_currentRowNumber);
            _eof = true;
            _bof = false;
            DfLogger.warn(dmRecordSet.class, String.format("Row %d is beyond last row.  dmRecordSet set to EOF.", rowNumber), null, null);
            throw new Exception(String.format("WARNING: Row %d is beyond last row.  dmRecordSet set to EOF.", rowNumber));
        }
        return _currentRow;
    }

    /**
     * Get the current record.
     *
     * @return IDfTypedObject object representing the current record.
     *
     * @see dmRecordSet#getCurrentRow() getCurrentRow
     *
     */
    @Deprecated
    public IDfTypedObject getRow() {
//        return _currentRow;
        return getCurrentRow();
    }

    /**
     * Get the current record.
     *
     * @return IDfTypedObject object representing the current record.
     *
     */
    public IDfTypedObject getCurrentRow() {
        return _currentRow;
    }

    /**
     * Get the current row number
     *
     * @return int with current row number
     *
     */
    public int getCurrentRowNumber() {
        return _currentRowNumber;
    }

    /**
     * Move the record set's internal pointer to just before the first record.
     *
     */
    @Deprecated
    public void resetBeginning() {
//        _currentRowNumber = -1;
//        _currentRow = null;
//        _eof = false;
//        _bof = true;
        resetToBeginning();
    }

    /**
     * Move the record set's internal pointer to just before the first record.
     *
     */
    public void resetToBeginning() {
        _currentRowNumber = -1;
        _currentRow = null;
        _eof = false;
        _bof = true;
    }

    /**
     * Move the record set's internal pointer to just past the last record.
     *
     */
    @Deprecated
    public void resetEnd() {
//        _currentRowNumber = _rowCount;
//        _currentRow = null;
//        _eof = true;
//        _bof = false;
        resetToEnd();
    }

    /**
     * Move the record set's internal pointer to just past the last record.
     *
     */
    public void resetToEnd() {
        _currentRowNumber = _rowCount;
        _currentRow = null;
        _eof = true;
        _bof = false;
    }

    /**
     * Add an IDfTypedObject to the record collection (after the last row).
     *
     * @param row IDfTypedObject to add to record set
     *
     */
    public void addRow(IDfTypedObject row) throws Exception {
        String rowColNames = "";
        
        try {
        
            // get column names in the row
            for (int i = 0; i < row.getAttrCount(); i++) {
                rowColNames += (row.getAttr(i).getName());
            }
        } catch (Exception e) {
            throw e;
        }
        
        // if column hashes match, add row to record set
        if (rowColNames.equalsIgnoreCase(_colNamesHash)) {
            _rows.add(row);
            _rowCount = _rows.size();
            _lastRow = _rowCount - 1;
        } else {
            DfLogger.warn(dmRecordSet.class, "Columns for row do not match record set. Row not added.", null, null);
            throw new Exception("Columns for row do not match record set. Row not added.");
        }
    }

    /**
     * Add multiple IDfTypedObjects to the record collection (after the last
     * row). This method calls addRow() so each IDfTypedObject can have its
     * rows evaluated before it is added to the record set.
     *
     * @param rows array of IDftypedObjects to add to record set
     *
     */
    public void addRows(ArrayList<IDfTypedObject> rows) throws Exception {
        
        try {
            for (IDfTypedObject row : rows){
                addRow(row);
            }
        } catch (Exception e) {
            DfLogger.warn(dmRecordSet.class,e.getMessage(),null,null);
            throw new Exception("Could not add rows: " + e.getMessage());
        }
    }

    /**
     * Return the record set as a List of IDfTypedObjects.
     *
     * Example using objects in List:
     *
     * List<IDftypedObject> list = dmRS.getRecordSetAsList(); while
     * (IDfTypedObject t : list) { String objId = t.getString("r_object_id");
     * ... }
     *
     * Note: You must use the IDfTypedObject.getString("") syntax to access data
     * from these objects. IDfTypedObject.getObjectId() will not return
     * meaningful data.
     *
     * @return List<IDfTypedObjects> of records in the record set.
     *
     */
    public List<IDfTypedObject> getRecordSetAsList() {
        return _rows;
    }

    /**
     * Return the record set as a Set of IDfTypedObjects. Note: the Set will
     * contain only unique objects (duplicates will not be included even if they
     * exist in the record set).
     *
     * Example using objects in Set:
     *
     * Set<IDftypedObject> set = dmRS.getRecordSetAsSet(); while (IDfTypedObject
     * t : set) { String objId = t.getString("r_object_id"); ... }
     *
     * Note: You must use the IDfTypedObject.getString("") syntax to access data
     * from these objects. IDfTypedObject.getObjectId() will not return
     * meaningful data.
     *
     * @return Set<IDfTypedObject> of records in the record set.
     *
     */
    public Set<IDfTypedObject> getRecordSetAsSet() {
        return new HashSet<IDfTypedObject>(_rows);
    }

    /**
     * Returns information about the record set
     *
     * @return String containing record set stats
     *
     */
    public String getRecordSetInfo() {
        StringBuilder sb = new StringBuilder();
        String[] dataTypes = new String[]{"BOOLEAN", "INTEGER", "STRING", "ID", "TIME", "DOUBLE", "UNDEFINED"};

        sb.append(getVersion());
        sb.append("\n");
        sb.append("---------------------------------------------------------------\n");
        sb.append(String.format("Row count: %d\n", getRowCount()));
        sb.append(String.format("Current row: %d\n", getCurrentRowNumber()));
        sb.append(String.format("Column count: %d\n", getColumnCount()));
        sb.append("Columns:\n");
        for (int i = 0; i < getColumnCount(); i++) {
            sb.append(String.format("\t %s (%s)\n", _columnDefs.get(i).getName(), dataTypes[_columnDefs.get(i).getDataType()]));
        }
        sb.append(String.format("is EOF: %s\n", Boolean.toString(isEOF())));
        sb.append(String.format("is BOF: %s\n", Boolean.toString(isBOF())));

        return sb.toString();
    }

    /**
     * Return version information about dmRecordSet class
     *
     * @return dmRecordSet version information
     *
     */
    public static String getVersion() {
        return _version;
    }
}

/*
 *  <SDG><
 */
