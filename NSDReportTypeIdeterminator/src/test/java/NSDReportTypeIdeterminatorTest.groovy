import org.apache.commons.io.IOUtils
import org.junit.jupiter.api.Test

import java.nio.file.Paths

class NSDReportTypeIdeterminatorTest {

    enum NSDReportTypes {
        SEEV_001_001_04("<MsgDefIdr>seev.001.001.04</MsgDefIdr>", "seev.001.001.04"),
        SEEV_002_001_04("<MsgDefIdr>seev.002.001.04</MsgDefIdr>", "seev.002.001.04"),
        SEEV_004_001_04("<MsgDefIdr>seev.004.001.04</MsgDefIdr>", "seev.004.001.04"),
        SEEV_006_001_04("<MsgDefIdr>seev.006.001.04</MsgDefIdr>", "seev.006.001.04"),
        SEEV_008_001_04("<MsgDefIdr>seev.008.001.04</MsgDefIdr>", "seev.008.001.04"),
        SEEV_031_001_04("<MsgDefIdr>seev.031.001.04</MsgDefIdr>", "seev.031.001.04"),
        SEEV_031_001_05("<MsgDefIdr>seev.031.001.05</MsgDefIdr>", "seev.031.001.05"),
        SEEV_032_001_04("<MsgDefIdr>seev.032.001.04</MsgDefIdr>", "seev.032.001.04"),
        SEEV_033_001_04("<MsgDefIdr>seev.033.001.04</MsgDefIdr>", "seev.033.001.04"),
        SEEV_034_001_04("<MsgDefIdr>seev.034.001.04</MsgDefIdr>", "seev.034.001.04"),
        SEEV_035_001_04("<MsgDefIdr>seev.035.001.04</MsgDefIdr>", "seev.035.001.04"),
        SEEV_036_001_05("<MsgDefIdr>seev.036.001.05</MsgDefIdr>", "seev.036.001.05"),
        SEEV_038_001_03("<MsgDefIdr>seev.038.001.03</MsgDefIdr>", "seev.038.001.03"),
        SEEV_039_001_04("<MsgDefIdr>seev.039.001.04</MsgDefIdr>", "seev.039.001.04"),
        SEEV_040_001_04("<MsgDefIdr>seev.040.001.04</MsgDefIdr>", "seev.040.001.04"),
        SEEV_041_001_04("<MsgDefIdr>seev.041.001.04</MsgDefIdr>", "seev.041.001.04"),
        SEMT_002_001_02("xsd:semt.002.001.02", "semt.002.001.02"),
        DISCLOSURE_CANCEL_REQUEST("<DISCLOSURE_CANCEL_REQUEST>", "disclosureChancelRequest"),
        DISCLOSURE_REQUEST("<DISCLOSURE_REQUEST>", "disclosureRequest"),
        FREE_FORMAT_MESSAGE_V02("<FREE_FORMAT_MESSAGE_V02>", "freeFormatMessageV02"),
        REGISTER_OF_SHAREHOLDERS_STATUS_ADVICE("<REGISTER_OF_SHAREHOLDERS_STATUS_ADVICE>", "registerOfShareholdersStatusAdvice");

        private final searchWord;
        private final reportType;

        NSDReportTypes(String searchWord, String reportType) {
            this.searchWord = searchWord;
            this.reportType = reportType;
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
            return searchWord
        }

        String getReportType() {
            return reportType
        }
    }

    @Test
    void test1() {
        def paths = Paths.get("C:\\projects\\SKB\\DIA-1561-EXPORT\\XML\\seev.036.001.051.XML")
        def file = new FileInputStream(paths.toFile())
        List<String> lines = IOUtils.readLines(file, "UTF-8");
        for (String line : lines) {
            def type = NSDReportTypes.findReportType(line)
            if (Objects.nonNull(type)) {
                println(type.getReportType());
            }
        }
    }
}
