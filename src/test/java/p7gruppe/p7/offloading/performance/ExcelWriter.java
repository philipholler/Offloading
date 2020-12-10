package p7gruppe.p7.offloading.performance;


import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import p7gruppe.p7.offloading.statistics.DataPoint;


import java.io.*;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class ExcelWriter {
    private static final String[] generalStatsHeader = {"", "Measurement"};
    private static String CONSTRUCTOR_PATH;
    private DecimalFormat decimalFormatter = (DecimalFormat) NumberFormat.getNumberInstance(Locale.US);

    public ExcelWriter() {
        // Apply patterns to the decimal formatter, that is used in the statistics files
        decimalFormatter.applyPattern("###.00");
        decimalFormatter.setRoundingMode(RoundingMode.HALF_UP);
        decimalFormatter.setGroupingUsed(false);
    }

    private static String getStatisticsPath(String subPath) {
        String path = System.getProperty("user.dir") + File.separator + "statistics" + File.separator + subPath;
        File file = new File(path);
        file.getParentFile().mkdirs();
        return path;
    }

    private static void createStatisticsFolder(){

    }

    public <T> void writeDataPoints(String relativePath, List<DataPoint<T>> dataPoints, String col1, String col2){
        String path = getStatisticsPath(relativePath);
        Workbook workbook = getOrCreateWorkbook(path);
        Sheet sheet = getOrCreateSheet(workbook, "main");
        createHeaders(workbook, sheet, new String[]{col1, col2});
        int i = 1;
        for (DataPoint<T> dp : dataPoints) {
            Row row = sheet.createRow(i++);
            row.createCell(0).setCellValue(dp.timestamp);
            row.createCell(1).setCellValue(String.valueOf(dp.value));
        }
        resizeAllColumnSizes(new String[]{col1, col2}, sheet);
        saveWorkbook(workbook, path);
    }

    public <T> void writeMultiDataPoints(String relativePath, List<List<DataPoint<T>>> dataPointLists, String[] headers){
        String path = getStatisticsPath(relativePath);
        Workbook workbook = getOrCreateWorkbook(path);
        Sheet sheet = getOrCreateSheet(workbook, "main");
        createHeaders(workbook, sheet, headers);

        int rowCount = 1;
        int columnCount = 1;
        for (List<DataPoint<T>> dataList : dataPointLists) {
            dataList.sort(Comparator.comparingLong((dp) -> dp.timestamp));
            for (DataPoint<T> dataPoint : dataList) {
                Row row = sheet.createRow(rowCount++);
                row.createCell(0).setCellValue(dataPoint.timestamp);
                row.createCell(columnCount).setCellValue(String.valueOf(dataPoint.value));
            }
            columnCount++;
        }

        resizeAllColumnSizes(headers, sheet);
        saveWorkbook(workbook, path);
    }

    private void createHeaders(Workbook workbook, Sheet sheet, String[] header) {
        // Create a Font for styling header cells
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerFont.setFontHeightInPoints((short) 14);
        headerFont.setColor(IndexedColors.BLACK.getIndex());

        // Create a CellStyle with the font
        CellStyle headerCellStyle = workbook.createCellStyle();
        headerCellStyle.setFont(headerFont);

        // Create a Row
        Row headerRow = sheet.createRow(0);

        // Create cells
        for(int i = 0; i < header.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(header[i]);
            cell.setCellStyle(headerCellStyle);
        }
    }

    private static Sheet getOrCreateSheet(Workbook workbook, String sheetName){
        Sheet sheet;
        if(workbook.getSheet(sheetName) != null) sheet = workbook.getSheet(sheetName);
        else sheet = workbook.createSheet(sheetName);
        return sheet;
    }

    private void resizeAllColumnSizes(String[] header, Sheet sheet){
        // Resize all columns to fit the content size
        for(int i = 0; i < header.length; i++) {
            sheet.autoSizeColumn(i);
        }
    }

    private static Workbook getOrCreateWorkbook(String pathToWorkBook) {
        Workbook workbook;
        if(!new File(pathToWorkBook).exists()){
            workbook = createExcelDocument(pathToWorkBook);
        } else workbook = openWorkBook(pathToWorkBook);

        return workbook;
    }

    private void saveWorkbook(Workbook workbook, String path) {
        // Write the output to a file
        FileOutputStream fileOut = null;
        try {

            fileOut = new FileOutputStream(path);
            workbook.write(fileOut);
            fileOut.close();

            // Closing the workbook
            workbook.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static Workbook openWorkBook(String pathToWorkBook) {
        InputStream is = null;
        try {
            is = new FileInputStream(pathToWorkBook);
            XSSFWorkbook workbook = new XSSFWorkbook(is);
            return workbook;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    private static Workbook createExcelDocument(String path) {
        Workbook workbook = new XSSFWorkbook();

        // Write the output to a file
        FileOutputStream fileOut = null;
        try {
            String pathToFile = path;
            fileOut = new FileOutputStream(pathToFile);
            workbook.write(fileOut);
            fileOut.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return workbook;
    }
}

