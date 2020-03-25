package com.converage.utils;

import com.converage.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.util.CollectionUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

@Slf4j
public class ExportUtils {

    /**
     * 最大工作表个数
     */
    private final static int SHEET_MAX_ROW_COUNT = 1048576;

    /**
     * 内存
     */
    public final static int SHEET_MAX_MEMORY_ROW_SIZE = 10000;

    /**
     * @param response
     * @param dataList
     * @param columnTitles
     * @param fileName
     */
    public static void exportToExcel07(HttpServletResponse response,
                                       List dataList, String[] columnTitles, String fileName) throws Exception {
        OutputStream out = null;
        try {
            setHeader(response, fileName);
            out = response.getOutputStream();
            SXSSFWorkbook sxsSFWorkbook = new SXSSFWorkbook(SHEET_MAX_MEMORY_ROW_SIZE);
            sxsSFWorkbook.setCompressTempFiles(true);
            sxsSFWorkbook.write(out);
            createSheets(out, sxsSFWorkbook, fileName, columnTitles, dataList);
        } finally {
            closeOutputStream(out);
        }
    }


    /**
     * @param response
     * @param fileName
     * @param dataSource
     */
    public static void exportToExcel07(HttpServletResponse response,
                                       String fileName, ExportDataSource dataSource){
        OutputStream out = null;
        SXSSFWorkbook workbook = null;
        try {
            setHeader(response, fileName);
            out = response.getOutputStream();
            workbook = new SXSSFWorkbook(SHEET_MAX_MEMORY_ROW_SIZE);
            workbook.setCompressTempFiles(true);
            createSheets(out, workbook, fileName, dataSource);
            workbook.write(out);
        } catch (IOException e) {
            throw new BusinessException("导出数据错误");
        } finally {
            closeOutputStream(out);
        }

    }

    /**
     * @param out
     * @param dataList
     * @param columnTitles
     * @param fileName
     * @throws IOException
     */
    public static void exportToExcel07(OutputStream out, List<Object> dataList,
                                       String[] columnTitles, String fileName) throws Exception {
        SXSSFWorkbook sxsSFWorkbook = new SXSSFWorkbook(
                Integer.valueOf(SHEET_MAX_MEMORY_ROW_SIZE));
        sxsSFWorkbook.setCompressTempFiles(true);
        sxsSFWorkbook.write(out);

        try {
            createSheets(out, sxsSFWorkbook, fileName, columnTitles, dataList);
        } finally {
            closeOutputStream(out);
        }
    }

    /**
     * @param out
     * @param workbook
     * @param sheetName
     * @param columnTitles
     * @param dataList
     * @throws IOException
     */
    private static void createSheets(OutputStream out, Workbook workbook,
                                     String sheetName, String[] columnTitles, List dataList)
            throws IOException {
        long sheetCount = 1;
        long total = dataList.size();
        if (total % SHEET_MAX_ROW_COUNT > 0) {
            sheetCount = total / SHEET_MAX_ROW_COUNT + 1;
        } else {
            sheetCount = total / SHEET_MAX_ROW_COUNT;
        }
        if (sheetCount == 1) {
            createSheet(workbook, sheetName, columnTitles, dataList);
        } else {
            for (int i = 0; i < sheetCount; i++) {
                createSheet(workbook, sheetName + (i + 1), columnTitles,
                        dataList);
                out.flush();
            }
        }

    }

    /**
     * @param out
     * @param workbook
     * @param sheetName
     * @param dataSource
     * @throws IOException
     */
    private static void createSheets(OutputStream out, Workbook workbook,
                                     String sheetName, ExportDataSource dataSource) throws IOException {

        List<Object> dataList = dataSource.load(1, SHEET_MAX_ROW_COUNT);

        long sheetCount;
        long totalCount;

        if (dataList.size() > SHEET_MAX_ROW_COUNT) {
            totalCount = dataSource.count();
        } else {
            totalCount = dataList.size();
        }

        if (totalCount % SHEET_MAX_ROW_COUNT > 0) {
            sheetCount = totalCount / SHEET_MAX_ROW_COUNT + 1;
        } else {
            sheetCount = totalCount / SHEET_MAX_ROW_COUNT;
        }

        createSheet(workbook, sheetName, dataSource.getColumnTitles(),
                dataList, dataSource);

        if (sheetCount > 1) {
            for (int i = 1; i <= sheetCount; i++) {
                createSheet(workbook, sheetName + (i + 1),
                        dataSource.getColumnTitles(), dataList, dataSource);
                // out.flush();
                dataList = dataSource.load(i, SHEET_MAX_ROW_COUNT);
            }
        }
    }

    public interface ExportDataSource<T> {

        String[] getColumnTitles();

        Object[] convert(T t);

        List load(int pageNo, int pageSize);

        long count();

    }

    /**
     * @param workbook
     * @param sheetName
     * @param columnTitles
     * @param list
     */
    private static void createSheet(Workbook workbook, String sheetName,
                                    String[] columnTitles, List list) {
        createSheet(workbook, sheetName, columnTitles, list, null);
    }

    /**
     * @param workbook
     * @param sheetName
     * @param columnTitles
     * @param list
     */
    private static void createSheet(Workbook workbook, String sheetName,
                                    String[] columnTitles, List list, ExportDataSource dataSource) {
        if (CollectionUtils.isEmpty(list)) {
            return;
        }

        Sheet sheet = workbook.createSheet(sheetName);
        // XSSFCellStyle cellStyle = (XSSFCellStyle) workbook.createCellStyle();

        Row row = sheet.createRow(0);

        for (int i = 0; i < columnTitles.length; i++) {
            Cell cell = row.createCell(i);
            cell.setCellType(CellType.STRING);
            cell.setCellValue(columnTitles[i]);// 写入表头内容
        }

        for (int i = 0; i < list.size(); ) {
            Object line = list.get(i);
            i++;
            row = sheet.createRow(i);
            Object[] ar = null;
            if (line == Object[].class) {
                ar = (Object[]) line;
            }
            if (dataSource != null) {
                ar = dataSource.convert(line);
            }
            if (ar == null) {
                continue;
            }

            for (int j = 0; j < ar.length; j++) {
                Cell cell = row.createCell(j);
                cell.setCellType(CellType.STRING);
                cell.setCellValue(toString(ar[j]));
            }
        }
    }

    private static String toString(Object value) {
        return value == null ? " " : value.toString();
    }

    private static void closeOutputStream(OutputStream out) {
        try {
            if (out != null)
                out.close();
        } catch (IOException e) {
            out = null;
            log.error("closeOutputStream error:", e);
        }
    }

    private static void setHeader(HttpServletResponse response, String fileName) {
        String encode = "export.xlsx";
        try {
            encode = URLEncoder.encode(fileName, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            log.error("setHeader error:", e);
        }
        response.setHeader("Content-Disposition", "attachment; filename=\""
                + encode + "\";");
        response.setHeader("Set-Cookie", "fileDownload=true; path=/");
        response.setContentType("application/octet-stream;");
        response.setCharacterEncoding("utf-8");
    }
}