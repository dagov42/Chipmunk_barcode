package ru.chipmunkbarcode.barcodeTypes;

import ru.chipmunkbarcode.exceptions.BarcodeException;

import java.awt.geom.Rectangle2D;
import java.math.BigInteger;

import static ru.chipmunkbarcode.barcodeTypes.HumanReadableLocation.NONE;
import static ru.chipmunkbarcode.barcodeTypes.HumanReadableLocation.TOP;

/**
 * <p>Implements USPS OneCode (also known as Intelligent Mail Barcode) according to USPS-B-3200F.
 *
 * <p>OneCode is a fixed length (65-bar) symbol which combines routing and customer information
 * in a single symbol. Input data consists of a 20 digit tracking code, followed by a dash (-),
 * followed by a delivery point ZIP code which can be 0, 5, 9 or 11 digits in length.
 *
 * @see <a href="https://ribbs.usps.gov/intelligentmail_mailpieces/documents/tech_guides/SPUSPSG.pdf">USPS OneCode Specification</a>
 */
public class UspsOneCode extends Symbol {

    /* The following lookup tables were generated using the code in Appendix C */

    /** Appendix D Table 1 - 5 of 13 characters */
    private static final int[] APPX_D_I = {
        0x001F, 0x1F00, 0x002F, 0x1E80, 0x0037, 0x1D80, 0x003B, 0x1B80, 0x003D, 0x1780,
        0x003E, 0x0F80, 0x004F, 0x1E40, 0x0057, 0x1D40, 0x005B, 0x1B40, 0x005D, 0x1740,
        0x005E, 0x0F40, 0x0067, 0x1CC0, 0x006B, 0x1AC0, 0x006D, 0x16C0, 0x006E, 0x0EC0,
        0x0073, 0x19C0, 0x0075, 0x15C0, 0x0076, 0x0DC0, 0x0079, 0x13C0, 0x007A, 0x0BC0,
        0x007C, 0x07C0, 0x008F, 0x1E20, 0x0097, 0x1D20, 0x009B, 0x1B20, 0x009D, 0x1720,
        0x009E, 0x0F20, 0x00A7, 0x1CA0, 0x00AB, 0x1AA0, 0x00AD, 0x16A0, 0x00AE, 0x0EA0,
        0x00B3, 0x19A0, 0x00B5, 0x15A0, 0x00B6, 0x0DA0, 0x00B9, 0x13A0, 0x00BA, 0x0BA0,
        0x00BC, 0x07A0, 0x00C7, 0x1C60, 0x00CB, 0x1A60, 0x00CD, 0x1660, 0x00CE, 0x0E60,
        0x00D3, 0x1960, 0x00D5, 0x1560, 0x00D6, 0x0D60, 0x00D9, 0x1360, 0x00DA, 0x0B60,
        0x00DC, 0x0760, 0x00E3, 0x18E0, 0x00E5, 0x14E0, 0x00E6, 0x0CE0, 0x00E9, 0x12E0,
        0x00EA, 0x0AE0, 0x00EC, 0x06E0, 0x00F1, 0x11E0, 0x00F2, 0x09E0, 0x00F4, 0x05E0,
        0x00F8, 0x03E0, 0x010F, 0x1E10, 0x0117, 0x1D10, 0x011B, 0x1B10, 0x011D, 0x1710,
        0x011E, 0x0F10, 0x0127, 0x1C90, 0x012B, 0x1A90, 0x012D, 0x1690, 0x012E, 0x0E90,
        0x0133, 0x1990, 0x0135, 0x1590, 0x0136, 0x0D90, 0x0139, 0x1390, 0x013A, 0x0B90,
        0x013C, 0x0790, 0x0147, 0x1C50, 0x014B, 0x1A50, 0x014D, 0x1650, 0x014E, 0x0E50,
        0x0153, 0x1950, 0x0155, 0x1550, 0x0156, 0x0D50, 0x0159, 0x1350, 0x015A, 0x0B50,
        0x015C, 0x0750, 0x0163, 0x18D0, 0x0165, 0x14D0, 0x0166, 0x0CD0, 0x0169, 0x12D0,
        0x016A, 0x0AD0, 0x016C, 0x06D0, 0x0171, 0x11D0, 0x0172, 0x09D0, 0x0174, 0x05D0,
        0x0178, 0x03D0, 0x0187, 0x1C30, 0x018B, 0x1A30, 0x018D, 0x1630, 0x018E, 0x0E30,
        0x0193, 0x1930, 0x0195, 0x1530, 0x0196, 0x0D30, 0x0199, 0x1330, 0x019A, 0x0B30,
        0x019C, 0x0730, 0x01A3, 0x18B0, 0x01A5, 0x14B0, 0x01A6, 0x0CB0, 0x01A9, 0x12B0,
        0x01AA, 0x0AB0, 0x01AC, 0x06B0, 0x01B1, 0x11B0, 0x01B2, 0x09B0, 0x01B4, 0x05B0,
        0x01B8, 0x03B0, 0x01C3, 0x1870, 0x01C5, 0x1470, 0x01C6, 0x0C70, 0x01C9, 0x1270,
        0x01CA, 0x0A70, 0x01CC, 0x0670, 0x01D1, 0x1170, 0x01D2, 0x0970, 0x01D4, 0x0570,
        0x01D8, 0x0370, 0x01E1, 0x10F0, 0x01E2, 0x08F0, 0x01E4, 0x04F0, 0x01E8, 0x02F0,
        0x020F, 0x1E08, 0x0217, 0x1D08, 0x021B, 0x1B08, 0x021D, 0x1708, 0x021E, 0x0F08,
        0x0227, 0x1C88, 0x022B, 0x1A88, 0x022D, 0x1688, 0x022E, 0x0E88, 0x0233, 0x1988,
        0x0235, 0x1588, 0x0236, 0x0D88, 0x0239, 0x1388, 0x023A, 0x0B88, 0x023C, 0x0788,
        0x0247, 0x1C48, 0x024B, 0x1A48, 0x024D, 0x1648, 0x024E, 0x0E48, 0x0253, 0x1948,
        0x0255, 0x1548, 0x0256, 0x0D48, 0x0259, 0x1348, 0x025A, 0x0B48, 0x025C, 0x0748,
        0x0263, 0x18C8, 0x0265, 0x14C8, 0x0266, 0x0CC8, 0x0269, 0x12C8, 0x026A, 0x0AC8,
        0x026C, 0x06C8, 0x0271, 0x11C8, 0x0272, 0x09C8, 0x0274, 0x05C8, 0x0278, 0x03C8,
        0x0287, 0x1C28, 0x028B, 0x1A28, 0x028D, 0x1628, 0x028E, 0x0E28, 0x0293, 0x1928,
        0x0295, 0x1528, 0x0296, 0x0D28, 0x0299, 0x1328, 0x029A, 0x0B28, 0x029C, 0x0728,
        0x02A3, 0x18A8, 0x02A5, 0x14A8, 0x02A6, 0x0CA8, 0x02A9, 0x12A8, 0x02AA, 0x0AA8,
        0x02AC, 0x06A8, 0x02B1, 0x11A8, 0x02B2, 0x09A8, 0x02B4, 0x05A8, 0x02B8, 0x03A8,
        0x02C3, 0x1868, 0x02C5, 0x1468, 0x02C6, 0x0C68, 0x02C9, 0x1268, 0x02CA, 0x0A68,
        0x02CC, 0x0668, 0x02D1, 0x1168, 0x02D2, 0x0968, 0x02D4, 0x0568, 0x02D8, 0x0368,
        0x02E1, 0x10E8, 0x02E2, 0x08E8, 0x02E4, 0x04E8, 0x0307, 0x1C18, 0x030B, 0x1A18,
        0x030D, 0x1618, 0x030E, 0x0E18, 0x0313, 0x1918, 0x0315, 0x1518, 0x0316, 0x0D18,
        0x0319, 0x1318, 0x031A, 0x0B18, 0x031C, 0x0718, 0x0323, 0x1898, 0x0325, 0x1498,
        0x0326, 0x0C98, 0x0329, 0x1298, 0x032A, 0x0A98, 0x032C, 0x0698, 0x0331, 0x1198,
        0x0332, 0x0998, 0x0334, 0x0598, 0x0338, 0x0398, 0x0343, 0x1858, 0x0345, 0x1458,
        0x0346, 0x0C58, 0x0349, 0x1258, 0x034A, 0x0A58, 0x034C, 0x0658, 0x0351, 0x1158,
        0x0352, 0x0958, 0x0354, 0x0558, 0x0361, 0x10D8, 0x0362, 0x08D8, 0x0364, 0x04D8,
        0x0383, 0x1838, 0x0385, 0x1438, 0x0386, 0x0C38, 0x0389, 0x1238, 0x038A, 0x0A38,
        0x038C, 0x0638, 0x0391, 0x1138, 0x0392, 0x0938, 0x0394, 0x0538, 0x03A1, 0x10B8,
        0x03A2, 0x08B8, 0x03A4, 0x04B8, 0x03C1, 0x1078, 0x03C2, 0x0878, 0x03C4, 0x0478,
        0x040F, 0x1E04, 0x0417, 0x1D04, 0x041B, 0x1B04, 0x041D, 0x1704, 0x041E, 0x0F04,
        0x0427, 0x1C84, 0x042B, 0x1A84, 0x042D, 0x1684, 0x042E, 0x0E84, 0x0433, 0x1984,
        0x0435, 0x1584, 0x0436, 0x0D84, 0x0439, 0x1384, 0x043A, 0x0B84, 0x043C, 0x0784,
        0x0447, 0x1C44, 0x044B, 0x1A44, 0x044D, 0x1644, 0x044E, 0x0E44, 0x0453, 0x1944,
        0x0455, 0x1544, 0x0456, 0x0D44, 0x0459, 0x1344, 0x045A, 0x0B44, 0x045C, 0x0744,
        0x0463, 0x18C4, 0x0465, 0x14C4, 0x0466, 0x0CC4, 0x0469, 0x12C4, 0x046A, 0x0AC4,
        0x046C, 0x06C4, 0x0471, 0x11C4, 0x0472, 0x09C4, 0x0474, 0x05C4, 0x0487, 0x1C24,
        0x048B, 0x1A24, 0x048D, 0x1624, 0x048E, 0x0E24, 0x0493, 0x1924, 0x0495, 0x1524,
        0x0496, 0x0D24, 0x0499, 0x1324, 0x049A, 0x0B24, 0x049C, 0x0724, 0x04A3, 0x18A4,
        0x04A5, 0x14A4, 0x04A6, 0x0CA4, 0x04A9, 0x12A4, 0x04AA, 0x0AA4, 0x04AC, 0x06A4,
        0x04B1, 0x11A4, 0x04B2, 0x09A4, 0x04B4, 0x05A4, 0x04C3, 0x1864, 0x04C5, 0x1464,
        0x04C6, 0x0C64, 0x04C9, 0x1264, 0x04CA, 0x0A64, 0x04CC, 0x0664, 0x04D1, 0x1164,
        0x04D2, 0x0964, 0x04D4, 0x0564, 0x04E1, 0x10E4, 0x04E2, 0x08E4, 0x0507, 0x1C14,
        0x050B, 0x1A14, 0x050D, 0x1614, 0x050E, 0x0E14, 0x0513, 0x1914, 0x0515, 0x1514,
        0x0516, 0x0D14, 0x0519, 0x1314, 0x051A, 0x0B14, 0x051C, 0x0714, 0x0523, 0x1894,
        0x0525, 0x1494, 0x0526, 0x0C94, 0x0529, 0x1294, 0x052A, 0x0A94, 0x052C, 0x0694,
        0x0531, 0x1194, 0x0532, 0x0994, 0x0534, 0x0594, 0x0543, 0x1854, 0x0545, 0x1454,
        0x0546, 0x0C54, 0x0549, 0x1254, 0x054A, 0x0A54, 0x054C, 0x0654, 0x0551, 0x1154,
        0x0552, 0x0954, 0x0561, 0x10D4, 0x0562, 0x08D4, 0x0583, 0x1834, 0x0585, 0x1434,
        0x0586, 0x0C34, 0x0589, 0x1234, 0x058A, 0x0A34, 0x058C, 0x0634, 0x0591, 0x1134,
        0x0592, 0x0934, 0x05A1, 0x10B4, 0x05A2, 0x08B4, 0x05C1, 0x1074, 0x05C2, 0x0874,
        0x0607, 0x1C0C, 0x060B, 0x1A0C, 0x060D, 0x160C, 0x060E, 0x0E0C, 0x0613, 0x190C,
        0x0615, 0x150C, 0x0616, 0x0D0C, 0x0619, 0x130C, 0x061A, 0x0B0C, 0x061C, 0x070C,
        0x0623, 0x188C, 0x0625, 0x148C, 0x0626, 0x0C8C, 0x0629, 0x128C, 0x062A, 0x0A8C,
        0x062C, 0x068C, 0x0631, 0x118C, 0x0632, 0x098C, 0x0643, 0x184C, 0x0645, 0x144C,
        0x0646, 0x0C4C, 0x0649, 0x124C, 0x064A, 0x0A4C, 0x0651, 0x114C, 0x0652, 0x094C,
        0x0661, 0x10CC, 0x0662, 0x08CC, 0x0683, 0x182C, 0x0685, 0x142C, 0x0686, 0x0C2C,
        0x0689, 0x122C, 0x068A, 0x0A2C, 0x0691, 0x112C, 0x0692, 0x092C, 0x06A1, 0x10AC,
        0x06A2, 0x08AC, 0x06C1, 0x106C, 0x06C2, 0x086C, 0x0703, 0x181C, 0x0705, 0x141C,
        0x0706, 0x0C1C, 0x0709, 0x121C, 0x070A, 0x0A1C, 0x0711, 0x111C, 0x0712, 0x091C,
        0x0721, 0x109C, 0x0722, 0x089C, 0x0741, 0x105C, 0x0742, 0x085C, 0x0781, 0x103C,
        0x0782, 0x083C, 0x080F, 0x1E02, 0x0817, 0x1D02, 0x081B, 0x1B02, 0x081D, 0x1702,
        0x081E, 0x0F02, 0x0827, 0x1C82, 0x082B, 0x1A82, 0x082D, 0x1682, 0x082E, 0x0E82,
        0x0833, 0x1982, 0x0835, 0x1582, 0x0836, 0x0D82, 0x0839, 0x1382, 0x083A, 0x0B82,
        0x0847, 0x1C42, 0x084B, 0x1A42, 0x084D, 0x1642, 0x084E, 0x0E42, 0x0853, 0x1942,
        0x0855, 0x1542, 0x0856, 0x0D42, 0x0859, 0x1342, 0x085A, 0x0B42, 0x0863, 0x18C2,
        0x0865, 0x14C2, 0x0866, 0x0CC2, 0x0869, 0x12C2, 0x086A, 0x0AC2, 0x0871, 0x11C2,
        0x0872, 0x09C2, 0x0887, 0x1C22, 0x088B, 0x1A22, 0x088D, 0x1622, 0x088E, 0x0E22,
        0x0893, 0x1922, 0x0895, 0x1522, 0x0896, 0x0D22, 0x0899, 0x1322, 0x089A, 0x0B22,
        0x08A3, 0x18A2, 0x08A5, 0x14A2, 0x08A6, 0x0CA2, 0x08A9, 0x12A2, 0x08AA, 0x0AA2,
        0x08B1, 0x11A2, 0x08B2, 0x09A2, 0x08C3, 0x1862, 0x08C5, 0x1462, 0x08C6, 0x0C62,
        0x08C9, 0x1262, 0x08CA, 0x0A62, 0x08D1, 0x1162, 0x08D2, 0x0962, 0x08E1, 0x10E2,
        0x0907, 0x1C12, 0x090B, 0x1A12, 0x090D, 0x1612, 0x090E, 0x0E12, 0x0913, 0x1912,
        0x0915, 0x1512, 0x0916, 0x0D12, 0x0919, 0x1312, 0x091A, 0x0B12, 0x0923, 0x1892,
        0x0925, 0x1492, 0x0926, 0x0C92, 0x0929, 0x1292, 0x092A, 0x0A92, 0x0931, 0x1192,
        0x0932, 0x0992, 0x0943, 0x1852, 0x0945, 0x1452, 0x0946, 0x0C52, 0x0949, 0x1252,
        0x094A, 0x0A52, 0x0951, 0x1152, 0x0961, 0x10D2, 0x0983, 0x1832, 0x0985, 0x1432,
        0x0986, 0x0C32, 0x0989, 0x1232, 0x098A, 0x0A32, 0x0991, 0x1132, 0x09A1, 0x10B2,
        0x09C1, 0x1072, 0x0A07, 0x1C0A, 0x0A0B, 0x1A0A, 0x0A0D, 0x160A, 0x0A0E, 0x0E0A,
        0x0A13, 0x190A, 0x0A15, 0x150A, 0x0A16, 0x0D0A, 0x0A19, 0x130A, 0x0A1A, 0x0B0A,
        0x0A23, 0x188A, 0x0A25, 0x148A, 0x0A26, 0x0C8A, 0x0A29, 0x128A, 0x0A2A, 0x0A8A,
        0x0A31, 0x118A, 0x0A43, 0x184A, 0x0A45, 0x144A, 0x0A46, 0x0C4A, 0x0A49, 0x124A,
        0x0A51, 0x114A, 0x0A61, 0x10CA, 0x0A83, 0x182A, 0x0A85, 0x142A, 0x0A86, 0x0C2A,
        0x0A89, 0x122A, 0x0A91, 0x112A, 0x0AA1, 0x10AA, 0x0AC1, 0x106A, 0x0B03, 0x181A,
        0x0B05, 0x141A, 0x0B06, 0x0C1A, 0x0B09, 0x121A, 0x0B11, 0x111A, 0x0B21, 0x109A,
        0x0B41, 0x105A, 0x0B81, 0x103A, 0x0C07, 0x1C06, 0x0C0B, 0x1A06, 0x0C0D, 0x1606,
        0x0C0E, 0x0E06, 0x0C13, 0x1906, 0x0C15, 0x1506, 0x0C16, 0x0D06, 0x0C19, 0x1306,
        0x0C23, 0x1886, 0x0C25, 0x1486, 0x0C26, 0x0C86, 0x0C29, 0x1286, 0x0C31, 0x1186,
        0x0C43, 0x1846, 0x0C45, 0x1446, 0x0C49, 0x1246, 0x0C51, 0x1146, 0x0C61, 0x10C6,
        0x0C83, 0x1826, 0x0C85, 0x1426, 0x0C89, 0x1226, 0x0C91, 0x1126, 0x0CA1, 0x10A6,
        0x0CC1, 0x1066, 0x0D03, 0x1816, 0x0D05, 0x1416, 0x0D09, 0x1216, 0x0D11, 0x1116,
        0x0D21, 0x1096, 0x0D41, 0x1056, 0x0D81, 0x1036, 0x0E03, 0x180E, 0x0E05, 0x140E,
        0x0E09, 0x120E, 0x0E11, 0x110E, 0x0E21, 0x108E, 0x0E41, 0x104E, 0x0E81, 0x102E,
        0x0F01, 0x101E, 0x100F, 0x1E01, 0x1017, 0x1D01, 0x101B, 0x1B01, 0x101D, 0x1701,
        0x1027, 0x1C81, 0x102B, 0x1A81, 0x102D, 0x1681, 0x1033, 0x1981, 0x1035, 0x1581,
        0x1039, 0x1381, 0x1047, 0x1C41, 0x104B, 0x1A41, 0x104D, 0x1641, 0x1053, 0x1941,
        0x1055, 0x1541, 0x1059, 0x1341, 0x1063, 0x18C1, 0x1065, 0x14C1, 0x1069, 0x12C1,
        0x1071, 0x11C1, 0x1087, 0x1C21, 0x108B, 0x1A21, 0x108D, 0x1621, 0x1093, 0x1921,
        0x1095, 0x1521, 0x1099, 0x1321, 0x10A3, 0x18A1, 0x10A5, 0x14A1, 0x10A9, 0x12A1,
        0x10B1, 0x11A1, 0x10C3, 0x1861, 0x10C5, 0x1461, 0x10C9, 0x1261, 0x10D1, 0x1161,
        0x1107, 0x1C11, 0x110B, 0x1A11, 0x110D, 0x1611, 0x1113, 0x1911, 0x1115, 0x1511,
        0x1119, 0x1311, 0x1123, 0x1891, 0x1125, 0x1491, 0x1129, 0x1291, 0x1131, 0x1191,
        0x1143, 0x1851, 0x1145, 0x1451, 0x1149, 0x1251, 0x1183, 0x1831, 0x1185, 0x1431,
        0x1189, 0x1231, 0x1207, 0x1C09, 0x120B, 0x1A09, 0x120D, 0x1609, 0x1213, 0x1909,
        0x1215, 0x1509, 0x1219, 0x1309, 0x1223, 0x1889, 0x1225, 0x1489, 0x1229, 0x1289,
        0x1243, 0x1849, 0x1245, 0x1449, 0x1283, 0x1829, 0x1285, 0x1429, 0x1303, 0x1819,
        0x1305, 0x1419, 0x1407, 0x1C05, 0x140B, 0x1A05, 0x140D, 0x1605, 0x1413, 0x1905,
        0x1415, 0x1505, 0x1423, 0x1885, 0x1425, 0x1485, 0x1443, 0x1845, 0x1483, 0x1825,
        0x1503, 0x1815, 0x1603, 0x180D, 0x1807, 0x1C03, 0x180B, 0x1A03, 0x1813, 0x1903,
        0x1823, 0x1883, 0x1843, 0x1445, 0x1249, 0x1151, 0x10E1, 0x0C46, 0x0A4A, 0x0952,
        0x08E2, 0x064C, 0x0554, 0x04E4, 0x0358, 0x02E8, 0x01F0
    };

    /** Appendix D Table II - 2 of 13 characters */
    private static final int[] APPX_D_II = {
        0x0003, 0x1800, 0x0005, 0x1400, 0x0006, 0x0C00, 0x0009, 0x1200, 0x000A, 0x0A00,
        0x000C, 0x0600, 0x0011, 0x1100, 0x0012, 0x0900, 0x0014, 0x0500, 0x0018, 0x0300,
        0x0021, 0x1080, 0x0022, 0x0880, 0x0024, 0x0480, 0x0028, 0x0280, 0x0030, 0x0180,
        0x0041, 0x1040, 0x0042, 0x0840, 0x0044, 0x0440, 0x0048, 0x0240, 0x0050, 0x0140,
        0x0060, 0x00C0, 0x0081, 0x1020, 0x0082, 0x0820, 0x0084, 0x0420, 0x0088, 0x0220,
        0x0090, 0x0120, 0x0101, 0x1010, 0x0102, 0x0810, 0x0104, 0x0410, 0x0108, 0x0210,
        0x0201, 0x1008, 0x0202, 0x0808, 0x0204, 0x0408, 0x0401, 0x1004, 0x0402, 0x0804,
        0x0801, 0x1002, 0x1001, 0x0802, 0x0404, 0x0208, 0x0110, 0x00A0
    };

    /** Appendix D Table IV - Bar-to-Character Mapping (reverse lookup) */
    private static final int[] APPX_D_IV = {
        67, 6, 78, 16, 86, 95, 34, 40, 45, 113, 117, 121, 62, 87, 18, 104, 41, 76, 57, 119, 115, 72, 97,
        2, 127, 26, 105, 35, 122, 52, 114, 7, 24, 82, 68, 63, 94, 44, 77, 112, 70, 100, 39, 30, 107,
        15, 125, 85, 10, 65, 54, 88, 20, 106, 46, 66, 8, 116, 29, 61, 99, 80, 90, 37, 123, 51, 25, 84,
        129, 56, 4, 109, 96, 28, 36, 47, 11, 71, 33, 102, 21, 9, 17, 49, 124, 79, 64, 91, 42, 69, 53,
        60, 14, 1, 27, 103, 126, 75, 89, 50, 120, 19, 32, 110, 92, 111, 130, 59, 31, 12, 81, 43, 55,
        5, 74, 22, 101, 128, 58, 118, 48, 108, 38, 98, 93, 23, 83, 13, 73, 3
    };

    private double moduleWidthRatio;
    private double shortHeightPercentage;
    private double longHeightPercentage;

    public UspsOneCode() {
        this.default_height = 8;
        this.humanReadableLocation = HumanReadableLocation.NONE;
        this.humanReadableAlignment = HumanReadableAlignment.LEFT; // spec section 2.4.2
        this.moduleWidthRatio = 1.43;
        this.shortHeightPercentage = 0.25;
        this.longHeightPercentage = 0.625;
    }

    /**
     * Sets the ratio of space width to bar width. The default value is {@code 1.43} (spaces are 43% wider than bars).
     *
     * @param moduleWidthRatio the ratio of space width to bar width
     */
    public void setModuleWidthRatio(double moduleWidthRatio) {
        this.moduleWidthRatio = moduleWidthRatio;
    }

    /**
     * Returns the ratio of space width to bar width.
     *
     * @return the ratio of space width to bar width
     */
    public double getModuleWidthRatio() {
        return moduleWidthRatio;
    }

    /**
     * Sets the percentage of the full symbol height used for short bars (0 - 1). The default value is {@code 0.25} (25% of full symbol height).
     *
     * @param shortHeightPercentage the percentage of the full symbol height used for short bars
     */
    public void setShortHeightPercentage(double shortHeightPercentage) {
        if (shortHeightPercentage < 0 || shortHeightPercentage > 1) {
            throw new IllegalArgumentException("Height percentage must be between 0 and 1.");
        }
        this.shortHeightPercentage = shortHeightPercentage;
    }

    /**
     * Returns the percentage of the full symbol height used for short bars.
     *
     * @return the percentage of the full symbol height used for short bars
     */
    public double getShortHeightPercentage() {
        return shortHeightPercentage;
    }

    /**
     * Sets the percentage of the full symbol height used for long bars (0 - 1). The default value is {@code 0.625} (62.5% of full symbol height).
     *
     * @param longHeightPercentage the percentage of the full symbol height used for long bars
     */
    public void setLongHeightPercentage(double longHeightPercentage) {
        if (longHeightPercentage < 0 || longHeightPercentage > 1) {
            throw new IllegalArgumentException("Height percentage must be between 0 and 1.");
        }
        this.longHeightPercentage = longHeightPercentage;
    }

    /**
     * Returns the percentage of the full symbol height used for long bars.
     *
     * @return the percentage of the full symbol height used for long bars
     */
    public double getLongHeightPercentage() {
        return longHeightPercentage;
    }

    @Override
    protected void encode() {
        String zip = "";
        String zipAdder;
        String tracker = "";
        int i, j;
        int length = content.length();
        BigInteger accum;
        BigInteger xReg;
        BigInteger mask;
        int uspsCrc;
        int[] codeword = new int[10];
        int[] characters = new int[10];
        boolean[] barMap = new boolean[130];
        char c;

        if (!content.matches("[0-9\u002D]+")) {
            throw new BarcodeException("Invalid characters in input");
        }

        if (length > 32) {
            throw new BarcodeException("Input too long");
        }

        /* separate the tracking code from the routing code */
        j = 0;
        for (i = 0; i < length; i++) {
            if (content.charAt(i) == '-') {
                j = 1;
            } else {
                if (j == 0) {
                    /* reading tracker */
                    tracker += content.charAt(i);
                } else {
                    /* reading zip code */
                    zip += content.charAt(i);
                }
            }
        }

        if (tracker.length() != 20) {
            throw new BarcodeException("Invalid length tracking code");
        }

        if (zip.length() > 11) {
            throw new BarcodeException("Invalid ZIP code");
        }

        /* *** Step 1 - Conversion of Data Fields into Binary Data *** */

        /* Routing code first */
        if (zip.length() > 0) {
            xReg = new BigInteger(zip);
        } else {
            xReg = new BigInteger("0");
        }

        /* add weight to routing code */
        if (zip.length() > 9) {
            zipAdder = "1000100001";
        } else {
            if (zip.length() > 5) {
                zipAdder = "100001";
            } else {
                if (zip.length() > 0) {
                    zipAdder = "1";
                } else {
                    zipAdder = "0";
                }
            }
        }

        accum = new BigInteger(zipAdder);
        accum = accum.add(xReg);
        accum = accum.multiply(BigInteger.valueOf(10));
        accum = accum.add(BigInteger.valueOf(Character.getNumericValue(tracker.charAt(0))));
        accum = accum.multiply(BigInteger.valueOf(5));
        accum = accum.add(BigInteger.valueOf(Character.getNumericValue(tracker.charAt(1))));
        for (i = 2; i < tracker.length(); i++) {
            accum = accum.multiply(BigInteger.valueOf(10));
            accum = accum.add(BigInteger.valueOf(Character.getNumericValue(tracker.charAt(i))));
        }

        /* *** Step 2 - Generation of 11-bit CRC on Binary Data *** */

        int[] byte_array = new int[13];
        for (i = 0; i < byte_array.length; i++) {
            mask = accum.shiftRight(96 - (8 * i));
            mask = mask.and(new BigInteger("255"));
            byte_array[i] = mask.intValue();
        }

        uspsCrc = USPS_MSB_Math_CRC11GenerateFrameCheckSequence(byte_array);

        /* *** Step 3 - Conversion from Binary Data to Codewords *** */

        /* start with codeword J which is base 636 */
        xReg = accum.mod(BigInteger.valueOf(636));
        codeword[9] = xReg.intValue();
        accum = accum.subtract(xReg);
        accum = accum.divide(BigInteger.valueOf(636));

        for (i = 8; i >= 0; i--) {
            xReg = accum.mod(BigInteger.valueOf(1365));
            codeword[i] = xReg.intValue();
            accum = accum.subtract(xReg);
            accum = accum.divide(BigInteger.valueOf(1365));
        }

        for (i = 0; i < 9; i++) {
            if (codeword[i] == 1365) {
                codeword[i] = 0;
            }
        }

        /* *** Step 4 - Inserting Additional Information into Codewords *** */

        codeword[9] = codeword[9] * 2;

        if (uspsCrc >= 1024) {
            codeword[0] += 659;
        }

        info("Codewords: ");
        for (i = 0; i < 10; i++) {
            infoSpace(codeword[i]);
        }
        infoLine();

        /* *** Step 5 - Conversion from Codewords to Characters *** */

        for (i = 0; i < 10; i++) {
            if (codeword[i] < 1287) {
                characters[i] = APPX_D_I[codeword[i]];
            } else {
                characters[i] = APPX_D_II[codeword[i] - 1287];
            }
        }

        for (i = 0; i < 10; i++) {
            if ((uspsCrc & (1 << i)) != 0) {
                characters[i] = 0x1FFF - characters[i];
            }
        }

        /* *** Step 6 - Conversion from Characters to the Intelligent Mail Barcode *** */

        for (i = 0; i < 10; i++) {
            for (j = 0; j < 13; j++) {
                if ((characters[i] & (1 << j)) == 0) {
                    barMap[APPX_D_IV[(13 * i) + j] - 1] = false;
                } else {
                    barMap[APPX_D_IV[(13 * i) + j] - 1] = true;
                }
            }
        }

        readable = formatHumanReadableText(content);
        pattern = new String[1];
        row_count = 1;
        row_height = new int[1];
        row_height[0] = -1;

        pattern[0] = "";
        for (i = 0; i < 65; i++) {
            c = 'T';
            if (barMap[i]) {
                c = 'D';
            }
            if (barMap[i + 65]) {
                c = 'A';
            }
            if (barMap[i] && barMap[i + 65]) {
                c = 'F';
            }
            pattern[0] += c;
        }

        infoLine("Encoding: " + pattern[0]);
    }

    private static int USPS_MSB_Math_CRC11GenerateFrameCheckSequence(int[] bytes) {

        int generatorPolynomial = 0x0F35;
        int frameCheckSequence = 0x07FF;
        int data;
        int byteIndex, bit;
        int byteArrayPtr = 0;

        /* Do most significant byte skipping the 2 most significant bits */
        data = bytes[byteArrayPtr] << 5;
        byteArrayPtr++;
        for (bit = 2; bit < 8; bit++) {
            if (((frameCheckSequence ^ data) & 0x400) != 0)
                frameCheckSequence = (frameCheckSequence << 1) ^ generatorPolynomial;
            else
                frameCheckSequence = (frameCheckSequence << 1);
            frameCheckSequence &= 0x7FF;
            data <<= 1;
        }

        /* Do rest of the bytes */
        for (byteIndex = 1; byteIndex < 13; byteIndex++) {
            data = bytes[byteArrayPtr] << 3;
            byteArrayPtr++;
            for (bit = 0; bit < 8; bit++) {
                if (((frameCheckSequence ^ data) & 0x0400) != 0) {
                    frameCheckSequence = (frameCheckSequence << 1) ^ generatorPolynomial;
                } else {
                    frameCheckSequence = (frameCheckSequence << 1);
                }
                frameCheckSequence &= 0x7FF;
                data <<= 1;
            }
        }

        return frameCheckSequence;
    }

    /**
     * <p>Formats the barcode content into the correct human-readable format, per section 2.4.3 of the spec:
     *
     * <p>The human-readable information, when required, shall consist of the 20-digit tracking code and the 5-, 9-, or 11-digit
     * routing code, if present. The fields of the tracking code, as defined in 2.1.3, shall be separated with a space added
     * between data fields. When the barcode contains a routing code, the 5-digit ZIP Code, the 4-digit add-on, and the
     * remaining 2 digits shall be separated with a space added between data fields.
     *
     * <p>Appendix F contains a good overview of the different IMb constructs / formats.
     *
     * @param content the content to be formatted
     * @return the formatted content
     */
    protected static String formatHumanReadableText(String content) {
        StringBuilder hrt = new StringBuilder(50);
        boolean mid9 = false; // 9-digit mailer ID instead of 6-digit mailer ID
        boolean tracing = true; // STID indicates Origin IMb Tracing Services (050, 052)
        boolean pimb = true; // barcode identifier (BI) is 94, indicating pIMb
        boolean mpe5 = false; // if MPE = 5, it's a CFS/RFS variant of pIMb
        int i = 0;
        for (char c : content.toCharArray()) {
            if (c < '0' || c > '9') {
                continue;
            }
            if (i == 5 && c == '9') {
                mid9 = true;
            }
            if ((i == 2 && c != '0') || (i == 3 && c != '5') || (i == 4 && c != '0' && c != '2')) {
                tracing = false;
            }
            if ((i == 0 && c != '9') || (i == 1 && c != '4')) {
                pimb = false;
            }
            if (i == 5 && c == '5') {
                mpe5 = true;
            }
            if ((i == 2) // BI -> STID
             || (i == 5) // STID -> ...
             || (i == 6 && pimb)
             || (i == 10 && pimb)
             || (i == 13 && pimb && !mpe5)
             || (i == 15 && pimb && !mpe5)
             || (i == 11 && !mid9 && !tracing && !pimb)
             || (i == 14 && mid9 && !tracing && !pimb)
             || (i == 20) // ... -> zip-5
             || (i == 25) // zip-5 -> zip-4
             || (i == 29)) { // zip-4 -> zip-2
                hrt.append(' ');
            }
            hrt.append(c);
            i++;
        }
        return hrt.toString().trim();
    }

    @Override
    protected void plotSymbol() {
        int xBlock, shortHeight, longHeight;
        double x, y, w, h, dx;

        rectangles.clear();
        texts.clear();

        int baseY;
        if (humanReadableLocation == TOP) {
            baseY = getTheoreticalHumanReadableHeight();
        } else {
            baseY = 0;
        }

        x = 0;
        w = moduleWidth;
        y = 0;
        h = 0;
        dx = (1 + moduleWidthRatio) * w;
        shortHeight = (int) (shortHeightPercentage * default_height);
        longHeight = (int) (longHeightPercentage * default_height);
        for (xBlock = 0; xBlock < pattern[0].length(); xBlock++) {

            switch (pattern[0].charAt(xBlock)) {
            case 'A':
                y = baseY;
                h = longHeight;
                break;
            case 'D':
                y = baseY + default_height - longHeight;
                h = longHeight;
                break;
            case 'F':
                y = baseY;
                h = default_height;
                break;
            case 'T':
                y = baseY + default_height - longHeight;
                h = shortHeight;
                break;
            }

            Rectangle2D.Double rect = new Rectangle2D.Double(x, y, w, h);
            rectangles.add(rect);

            x += dx;
        }

        symbol_width = (int) Math.ceil(((pattern[0].length() - 1) * dx) + w); // final bar doesn't need extra whitespace
        symbol_height = default_height;

        if (humanReadableLocation != NONE && !readable.isEmpty()) {
            double baseline;
            if (humanReadableLocation == TOP) {
                baseline = fontSize;
            } else {
                baseline = symbol_height + fontSize;
            }
            texts.add(new TextBox(0, baseline, symbol_width, readable, humanReadableAlignment));
        }
    }
}
