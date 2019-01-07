# Delete Functionality
1. To implement "archiveExpiredSnips" method to mark expired snips.
  Update last-modified-date when updating archive status. -- done
2. To implement "deleteSnipFromStorage" method to remove snips that are in archived status from past 7 days. -- done
3. Make sure to delete only the file and not the directory itself. --done

# @Qualifier to listen from application.yml file

# Externalize Configuration file i.e. application.yml
1. Try running yml file using --spring.config.location command while running via mvn. --done

# Implement HDFS, AWS and GCP Storage mechanism

# read resource snipSchema using getResource()