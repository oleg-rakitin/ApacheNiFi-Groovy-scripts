import org.apache.commons.io.IOUtils
import org.apache.nifi.flowfile.FlowFile

/***
 This script defines the report type and sets the ReportType attribute to FlowFile.
 This block contains supported types of reports that we receive from NSD in the form of xml files.
 To add a new type, add an entry by analogy with the others and write inside as a parameter the phrase
 that the report contains, by which it can be identified.
 ***/

enum NSDReportTypes {
    SEEV_001_001_04("<MsgDefIdr>seev.001.001.04</MsgDefIdr>"),
    SEEV_002_001_04("<MsgDefIdr>seev.002.001.04</MsgDefIdr>"),
    SEEV_004_001_04("<MsgDefIdr>seev.004.001.04</MsgDefIdr>"),
    SEEV_006_001_04("<MsgDefIdr>seev.006.001.04</MsgDefIdr>"),
    SEEV_008_001_04("<MsgDefIdr>seev.008.001.04</MsgDefIdr>"),
    SEEV_031_001_04("<MsgDefIdr>seev.031.001.04</MsgDefIdr>"),
    SEEV_031_001_05("<MsgDefIdr>seev.031.001.05</MsgDefIdr>"),
    SEEV_032_001_04("<MsgDefIdr>seev.032.001.04</MsgDefIdr>"),
    SEEV_033_001_04("<MsgDefIdr>seev.033.001.04</MsgDefIdr>"),
    SEEV_034_001_04("<MsgDefIdr>seev.034.001.04</MsgDefIdr>"),
    SEEV_035_001_04("<MsgDefIdr>seev.035.001.04</MsgDefIdr>"),
    SEEV_036_001_05("<MsgDefIdr>seev.036.001.05</MsgDefIdr>"),
    SEEV_038_001_03("<MsgDefIdr>seev.038.001.03</MsgDefIdr>"),
    SEEV_039_001_04("<MsgDefIdr>seev.039.001.04</MsgDefIdr>"),
    SEEV_040_001_04("<MsgDefIdr>seev.040.001.04</MsgDefIdr>"),
    SEEV_041_001_04("<MsgDefIdr>seev.041.001.04</MsgDefIdr>"),
    SEMT_002_001_02("xsd:semt.002.001.02"),
    DISCLOSURE_CANCEL_REQUEST("<DISCLOSURE_CANCEL_REQUEST>"),
    DISCLOSURE_REQUEST("<DISCLOSURE_REQUEST>"),
    FREE_FORMAT_MESSAGE_V02("<FREE_FORMAT_MESSAGE_V02>"),
    REGISTER_OF_SHAREHOLDERS_STATUS_ADVICE("<REGISTER_OF_SHAREHOLDERS_STATUS_ADVICE>");

    private final String searchWord;

    NSDReportTypes(String searchWord) {
        this.searchWord = searchWord;
    }

    static NSDReportTypes findReportType(String line) {
        if (Objects.isNull(line)) {
            return null;
        }

        for (NSDReportTypes report : values()) {
            if (line.contains(report.getSearchWord())) {
                return report;
            }
        }
        return null;
    }

    String getSearchWord() {
        return searchWord;
    }
}

try {
    analyzeReport()
} catch (Exception e) {
    Log.error(e);
    session.transfer(flowFile, REL_ERROR);
}

void analyzeReport() {
    FlowFile flowFile = session.get()
    if (flowFile == null) {
        return;
    }
    String reportType = null;
    session.read(flowFile, { inputStream ->
        List<String> lines = IOUtils.readLines(inputStream, "UTF-8");
        for (String line : lines) {
            def type = NSDReportTypes.findReportType(line)
            if (Objects.nonNull(type)) {
                reportType = type;
                break;
            }
        }
    })
    if (Objects.nonNull(reportType)) {
        flowFile = session.putAttribute(flowFile, 'reportType', reportType)
    }
    session.transfer(flowFile, REL_SUCCESS);
}
