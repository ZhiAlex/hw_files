import com.codeborne.pdftest.PDF;
import com.codeborne.xlstest.XLS;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import static com.codeborne.pdftest.assertj.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CheckFilesTest {

    @Test
    public void checkFilesTest() throws IOException, CsvException {

        ZipFile zipFile = new ZipFile("src\\test\\resources\\files.zip");

        Enumeration<? extends ZipEntry> entries = zipFile.entries();

        while (entries.hasMoreElements()) {
            ZipEntry zipEntry = entries.nextElement();
            String extension = zipEntry.getName().split("\\.")[1];
            InputStream stream;
            if (extension.equals("pdf")) {
                stream = zipFile.getInputStream(zipEntry);
                PDF pdf = new PDF(stream);
                assertTrue((pdf.text).contains("This is a test PDF document."));
            }
            if (extension.equals("xlsx")) {
                stream = zipFile.getInputStream(zipEntry);
                XLS xls = new XLS(stream);
                assertEquals(123.0, xls.excel.getSheetAt(0).getRow(0).getCell(0).getNumericCellValue());
            }
            if (extension.equals("csv")) {
                stream = zipFile.getInputStream(zipEntry);
                CSVReader reader = new CSVReader(new InputStreamReader(stream));
                List<String[]> csvList = reader.readAll();
                assertThat(csvList.get(0))
                        .contains("test", "test");
                reader.close();
            }
        }
        zipFile.close();
    }
}
