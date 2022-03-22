import pandas as pd

def tmd_load_dataset(path_dataset = "./input/dataset1.csv"):
    data = pd.read_csv(path_dataset, index_col=0)
    # remove the first 4 columns and the last one
    data = data.iloc[:, 4:-1]
    X = data.iloc[:, :-1]
    return X

def har_load_dataset(path_dataset = "./input/dataset2.csv"):
    data = pd.read_csv(path_dataset)
    X = data.iloc[:, :-2]
    return X

def manage_df(df):
    df=df.fillna(df.median())
    df=(df-df.min())/(df.max()-df.min())
    print(df)
    return df

for X in zip ([tmd_load_dataset(),har_load_dataset()],[0,1]):
    df = manage_df(X[0])
    df.to_csv("./output/dataset"+str(X[1])+".csv")
