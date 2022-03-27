import pandas as pd
from sklearn.cluster import KMeans
import pandas as pd
import matplotlib.pyplot as plt
from sklearn import metrics


df=pd.read_csv("./input/dataset1.csv")

df =df.iloc[:,1:]
print(df.head())
sse = {}
for k in range(2, 20):
    kmeans = KMeans(n_clusters=k, max_iter=1000).fit(df)
    df["clusters"] = kmeans.labels_
    #print(data["clusters"])
    #sse[k] = metrics.calinski_harabasz_score(df, kmeans.labels_) 
    sse[k] = kmeans.inertia_
plt.figure()
plt.plot(list(sse.keys()), list(sse.values()))
plt.xlabel("Number of cluster")
plt.ylabel("WCSS")
plt.show()