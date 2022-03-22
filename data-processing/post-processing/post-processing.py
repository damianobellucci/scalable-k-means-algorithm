import glob
from numpy import double
import pandas as pd
import numpy as np

path_input = "./input"
path_output = "./output"

files = glob.glob(path_input+"/*")

def df_generator(rows):
    df = pd.DataFrame(np.array(rows),columns=['n_clusters','mean_wcss','iterations','time'])

    return df

def parse_line(line):
    values = line.replace("(","").replace(")","").replace("\n","").split(",")
    return list(map(lambda x:(str(double(x).round(2))).replace(".",","),values))

for file in files:
    fp = open(file, 'r')
    lines = fp.readlines()
    df_rows = list(map(parse_line,lines))
    df  = df_generator(df_rows)
    df.to_csv(path_output+file[file.rfind("/"):]+".csv")