import com.linuxense.javadbf.DBFReader
import com.linuxense.javadbf.DBFRow
import org.apache.commons.io.IOUtils
import org.apache.nifi.flowfile.FlowFile

import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream

try {
    analyze()
} catch (Exception e) {
    Log.error(e);
    session.transfer(flowFile, REL_ERROR);
}

private void analyze() throws Exception {
    FlowFile flowFile = session.get()
    if (flowFile == null) {
        return;
    }
    boolean isContainsDbf = false;
    String reportType = null; ;
    session.read(flowFile, { inputStream ->
        ZipInputStream zipIn = new ZipInputStream(inputStream);
        ZipEntry entry;
        while ((entry = zipIn.getNextEntry()) != null) {
            if (!entry.isDirectory()) {
                if ("reporth.dbf" == entry.getName()) {
                    isContainsDbf = true;
                    InputStream stream = convertZipInputStreamToInputStream(zipIn)
                    reportType = getReportTypeFromDBF(stream)
                    if (Objects.isNull(reportType)) {
                        throw new IllegalStateException("reporth.dbf file is not contains OUT_FORM value");
                    }
                }
            }
        }
        zipIn.closeEntry();
    })
    if (Objects.nonNull(reportType)) {
        flowFile = session.putAttribute(flowFile, 'reportType', reportType)
    }
    flowFile = session.putAttribute(flowFile, 'isContainsDBF', String.valueOf(isContainsDbf))
    session.transfer(flowFile, REL_SUCCESS);
}

private static InputStream convertZipInputStreamToInputStream(ZipInputStream inp) throws IOException {
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    IOUtils.copy(inp, out);
    InputStream is = new ByteArrayInputStream(out.toByteArray());
    return is;
}

private static String getReportTypeFromDBF(InputStream input) {
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