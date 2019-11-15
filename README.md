IDfCollection objects: we all know and use them, and we all wish they were more functional.  For example, you can’t directly create an IDfCollection object, but it sure would be useful if you could.  You can’t add columns or rows to an IDfCollection object or even change the values they contain, but it sure would be useful if you could.  How about traversing forward and backward or randomly through an IDfCollection’s rows, or submitting an IDfCollection for update?  In many ways, the IDfCollection class is a direct encapsulation of the collection used by the Documentum API (DMCL) years ago.  That’s really unfortunate, because it could potentially be so much more.

I created the dmRecordSet class as an alternative to the IDfCollection class.  dmRecordSet treats query results more like main stream record sets that can be traversed, appended, examined, rewound, etc.  The included tutorial and test cases provide detailed examples.

The dmRecordSet provides the following methods:
* public dmRecordSet(IDfCollection col) throws DfException => instantiation
* public int getRowCount() => count number of rows in record set (i.e., number or results)
* public int getColumnCount() => count number of columns in record set
* public ArrayList<IDfAttr> getColumnDefs() => get the column definitions so you know the type of each column
* public boolean isBOF() => is beginning of file?
* public boolean isEOF() => is end of file?
* public boolean isEmpty() => is the record set empty (i.e., no results)?
* public boolean hasNext() => is there another row to process?
* public IDfTypedObject getNextRow() throws Exception => get the contents of the next row as an IDfTypedObject so you can use all the methods associated with IDfTypedObject or cast it to another DFC class.
* public boolean hasPrevious() => is there a next row if you are processing the record set backwards?
* public IDfTypedObject getPreviousRow() throws Exception => same as betNextRow(), but for backwards processing
* public IDfTypedObject getFirstRow() => get the first row as an IDfTypedObject
* public IDfTypedObject getLastRow() => get the last row as an IDfTypedObject
* public IDfTypedObject getRow(int rowNumber) throws Exception => get a specific result indexed by row number
* public IDfTypedObject getCurrentRow() => get the current row as an IDfTypedObject
* public int getCurrentRowNumber() => get the location of the current row pointer
* public void resetToBeginning() => reset row pointer to the beginning of the file
* public void resetToEnd() => reset the row pointer to the end of the file

