import glob
from numpy import column_stack, double
import pandas as pd
import numpy as np

path_input = "./input"
path_output = "./output"

columns = ["#clusters","wcss","time_wcss","mean_wcss","time_mean_wcss","calinski","time_calinski","#iterations","time_clusterization"]

files = glob.glob(path_input+"/*")

def df_generator(rows):
    df = pd.DataFrame(np.array(rows),columns=columns)

    return df

def parse_line(line):
    values = line.replace("(","").replace(")","").replace("\n","").split(",")
    return list(map(lambda x:(str(double(x).round(5))).replace(".",","),values))

for file in files:
    fp = open(file, 'r')
    lines = fp.readlines()
    df_rows = list(map(parse_line,lines))
    df  = df_generator(df_rows)
    df.to_csv(path_output+file[file.rfind("/"):]+".csv")