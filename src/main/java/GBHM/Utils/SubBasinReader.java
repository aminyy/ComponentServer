/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package GBHM.Utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

/**
 *
 * @author longyinping
 */
public class SubBasinReader {

    // kfs
    int nlevel;
    int[] kfs1;
    int[][] kfs2;
    int[][][] kfs3;
    int nsub;
    int[] subnumber;
    String[] subbasin;
    int[] upStreamID1;
    int[] upStreamID2;

    public SubBasinReader(String kfsFileName) throws IOException {
        ReadKFS(kfsFileName);
        SubName();

        upStreamID1 = new int[nsub];
        upStreamID2 = new int[nsub];

        for (int i = 0; i < nsub; i++) {
            upStreamID1[i] = this.findUpstream(subbasin[i], subbasin)[0];
            upStreamID2[i] = this.findUpstream(subbasin[i], subbasin)[1];
        }

    }

    public int getSubNum() {
        return this.nsub;
    }

    public int[] getSubID() {
        return subnumber;
    }

    public String[] getSubName() {
        return subbasin;
    }

    public int[] getUpstreamID1() {
        return this.upStreamID1;
    }

    public int[] getUpstreamID2() {
        return this.upStreamID2;
    }

    // read kfs.dat file
    private void ReadKFS(String kfsFileName) throws IOException {
        String line;
        String[] split;
        int level, l1, l2, l3;

        kfs1 = new int[9];
        kfs2 = new int[9][9];
        kfs3 = new int[9][9][9];

        FileReader kfsfile = new FileReader(kfsFileName);
        BufferedReader kfsin = new BufferedReader(kfsfile);

        line = StringUtil.delSpace(kfsin.readLine());
        split = line.split(" +");
        nlevel = Integer.parseInt(split[1]);

        kfsin.readLine();

        for (level = 1; level <= nlevel - 1; level++) {
            if (level == 1) {
                kfsin.readLine();
                line = StringUtil.delSpace(kfsin.readLine());
                split = line.split(" +|, +");
                for (l1 = 0; l1 <= 8; l1++) {
                    kfs1[l1] = Integer.parseInt(split[l1 + 1]);
                }
            } else if (level == 2) {
                kfsin.readLine();
                for (l1 = 0; l1 <= 8; l1++) {
                    if (kfs1[l1] == 1) {
                        line = StringUtil.delSpace(kfsin.readLine());
                        split = line.split(" +|, +");
                        for (l2 = 0; l2 <= 8; l2++) {
                            kfs2[l1][l2] = Integer.parseInt(split[l2 + 1]);
                        }
                    } else {
                        kfsin.readLine();
                    }
                }
            } else if (level == 3) {
                kfsin.readLine();
                for (l1 = 0; l1 <= 8; l1++) {
                    if (kfs1[l1] == 1) {
                        for (l2 = 0; l2 <= 8; l2++) {
                            if (kfs2[l1][l2] == 1) {
                                line = StringUtil.delSpace(kfsin.readLine());
                                split = line.split(" +|, +");
                                for (l3 = 0; l3 <= 8; l3++) {
                                    kfs3[l1][l2][l3] = Integer
                                            .parseInt(split[l3 + 1]);
                                }
                            }
                        }
                    }
                }

            }

        }
        kfsin.close();
        kfsfile.close();
    }

    private void SubName() {
        int l1, l2, l3, i;
        String[] subbasin1;
        int[] subnumber1;

        subbasin1 = new String[9 * 9 * 9];
        subnumber1 = new int[9 * 9 * 9];

        nsub = 0;
        for (l1 = 9; l1 >= 1; l1--) {
            if (kfs1[l1 - 1] == 0) {
                nsub = nsub + 1;
                subbasin1[nsub] = "ws" + (char) (48 + l1) + (char) (48 + 0)
                        + (char) (48 + 0);
                subnumber1[nsub] = l1 * 100;
            } else {
                for (l2 = 9; l2 >= 1; l2--) {
                    if (kfs2[l1 - 1][l2 - 1] == 0) {
                        nsub = nsub + 1;
                        subbasin1[nsub] = "ws" + (char) (48 + l1)
                                + (char) (48 + l2) + (char) (48 + 0);
                        subnumber1[nsub] = l1 * 100 + l2 * 10;
                    } else {
                        for (l3 = 9; l3 >= 1; l3--) {
                            nsub = nsub + 1;
                            subbasin1[nsub] = "ws" + (char) (48 + l1)
                                    + (char) (48 + l2) + (char) (48 + l3);
                            subnumber1[nsub] = l1 * 100 + l2 * 10 + l3;
                        }
                    }
                }
            }
        }

        subbasin = new String[nsub];
        subnumber = new int[nsub];

        for (i = 0; i < nsub; i++) {
            subbasin[i] = subbasin1[i + 1];
            subnumber[i] = subnumber1[i + 1];
        }
    }

    private int[] findUpstream(String subName, String[] subNames) {

        int l1, l2, l3;
        int[] upstreamID = new int[2];
        char[] L;
        String up1 = "";
        String up2 = "";

        L = subName.toCharArray();

        l1 = L[2] - 48;
        l2 = L[3] - 48;
        l3 = L[4] - 48;

        ArrayList<String> subs = new ArrayList();
        subs.addAll(Arrays.asList(subNames));

        if (l3 != 0 && l3 != 9) {
            if (l3 % 2 != 0) {
                up1 = "ws" + L[2] + L[3] + (char) (48 + l3 + 1);
                up2 = "ws" + L[2] + L[3] + (char) (48 + l3 + 2);
            }
        } else {//l3==0,9
            if (l2 != 0 && l2 != 9) {
                if (l2 % 2 != 0) {
                    up1 = "ws" + L[2] + (char) (48 + l2 + 1) + (char) (48 + 0);
                    if (subs.indexOf(up1) == -1) {
                        up1 = "ws" + L[2] + (char) (48 + l2 + 1) + (char) (48 + 1);
                    }
                    up2 = "ws" + L[2] + (char) (48 + l2 + 2) + (char) (48 + 0);
                    if (subs.indexOf(up2) == -1) {
                        up2 = "ws" + L[2] + (char) (48 + l2 + 2) + (char) (48 + 1);
                    }
                }
            } else {//l2=0,9;l3=0,9
                if (l1 != 9 && l1 % 2 != 0) {
                    up1 = "ws" + (char) (48 + l1 + 1) + (char) (48 + 0) + (char) (48 + 0);
                    if (subs.indexOf(up1) == -1) {
                        up1 = "ws" + (char) (48 + l1 + 1) + (char) (48 + 1) + (char) (48 + 0);
                        if (subs.indexOf(up1) == -1) {
                            up1 = "ws" + (char) (48 + l1 + 1) + (char) (48 + 1) + (char) (48 + 1);
                        }
                    }
                    up2 = "ws" + (char) (48 + l1 + 2) + (char) (48 + 0) + (char) (48 + 0);
                    if (subs.indexOf(up2) == -1) {
                        up2 = "ws" + (char) (48 + l1 + 2) + (char) (48 + 1) + (char) (48 + 0);
                        if (subs.indexOf(up2) == -1) {
                            up2 = "ws" + (char) (48 + l1 + 2) + (char) (48 + 1) + (char) (48 + 1);
                        }
                    }
                }
            }
        }
        upstreamID[0] = subs.indexOf(up1);
        upstreamID[1] = subs.indexOf(up2);

        return upstreamID;
    }
}
