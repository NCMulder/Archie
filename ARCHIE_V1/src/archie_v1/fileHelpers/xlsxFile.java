/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package archie_v1.fileHelpers;

import java.nio.file.Path;

/**
 *
 * @author N.C. Mulder <n.c.mulder at students.uu.nl>
 */
public class xlsxFile extends FileHelper {

    //Create a better addable adding method. Also helpful for removing, perhabs.
    public xlsxFile(Path filePath) {
        super(filePath);
        String author = metadata.get("creator");
        setRecordThroughTika(MetadataKey.CreatorGivenName, "creator");
        setRecordThroughTika(MetadataKey.CreatorFamilyName, "creator");
        String contributor = metadata.get("Last-Author");
        if(null!=author && null!=contributor && !author.equals(contributor)){
            setRecordThroughTika(MetadataKey.ContributorGivenName, "Last-Author");
            setRecordThroughTika(MetadataKey.ContributorFamilyName, "Last-Author");
        }
    }

}
