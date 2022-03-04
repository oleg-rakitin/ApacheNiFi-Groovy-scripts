import com.linuxense.javadbf.DBFReader
import com.linuxense.javadbf.DBFRow
import org.apache.commons.io.IOUtils
import org.junit.jupiter.api.Test

import java.nio.file.Paths
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream

class ZipAnalyzer {

    @Test
    void test() {
        String reportType = getReportTypeFromZip()

    }

    private String getReportTypeFromZip() {
        def get = Paths.get("C:\\projects\\SKB\\DIA-1561\\MS1011.zip")
        println(get.getFileName());
        InputStream stream = new FileInputStream(get.toFile());
        ZipInputStream zipIsZ = new ZipInputStream(stream);

        try {
            ZipEntry entry;
            String reportType = null;
            while ((entry = zipIsZ.getNextEntry()) != null) {
                if (!entry.isDirectory()) {
                    if ("reporth.dbf" == entry.getName()) {
                        def inputStream = convertZipInputStreamToInputStream(zipIsZ)
                        reportType = getReportTypeFromDBF(inputStream)
                        if (Objects.isNull(reportType)) {
                            throw new IllegalStateException("reporth.dbf file is not contains OUT_FORM value");
                        }
                    }
                    return reportType;
                }
            }
        }
        finally {
            zipIsZ.close();
        }
        return null;
    }

    private InputStream convertZipInputStreamToInputStream(ZipInputStream inp) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        IOUtils.copy(inp, out);
        InputStream is = new ByteArrayInputStream(out.toByteArray());
        return is;
    }

    private String getReportTypeFromDBF(InputStream input) {
        DBFReader reader = new DBFReader(input);

        DBFRow row;
        def reportType;
        while ((row = reader.nextRow()) != null) {
            reportType = row.getString("OUT_FORM");
            if (Objects.nonNull(reportType)) {
                return reportType;
            }
        }
        return null;
    }
}
