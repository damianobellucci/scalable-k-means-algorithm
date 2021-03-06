# Scalable K-means algorithm 

## Introduzione
L'obiettivo di questo progetto è eseguire uno studio di data analysis per studiare l'efficacia dell'algoritmo K-means, implementato per essere eseguito su risorse scalabili, per problemi di transport mode detection e human activity recognition, su dataset fatti da dati provenienti da sensori di smartphone. 
In particolare, si è cercato di capire se a partire da questi dati è possibile fare clusterizzazione per individuare i cluster di dati relativi ai tipi di trasporto oppure alle attività umane.
Possibili risvolti di questo studio possono essere il riconoscimento di tipi diversi modalità di trasporto/attività umane a partire da dati di sensori, senza però conoscerne la denominazione. 

Lo studio è stato svolto adottando tecniche di analisi dei dati scalabili, tramite Spark, una libreria che permette di eseguire computazione parallela scrivendo codice sequenziale, con relativa gestione delle risorse di calcolo. Infatti lo stesso codice è stato mandado in esecuzione su diverse risorse di calcolo, dalla macchina locale ai cluster con più nodi di calcolo del servizio di cloud computing Google Cloud Platform. 
L'esecuzione su risorse scalabili permette di capire se lo studio può essere effettuato, anche in futuro con diversi dataset, più efficacamente avendo a disposizione più capacità di calcolo.

Il linguaggio di programmazione utilizzato è Scala, un linguaggio tipato staticamente la cui compilazione del codice produce Java Bytecode eseguibile sulla Java Virtual Machine. Supporta la programmazione object oriented e la programmazione funzionale.


## Punti chiave del progetto
<ul>
  <li> Implementazione algoritmo K-means per dataset con n-features</li>
  <li> Implementazione calcolo degli indici WCSS, Calinski Harabasz e mean WCSS, che sono indici per valutare la qualità della clusterizzazione</li>
  <li> Raccolta dati ottenuti con algoritmo k-means implementato, usando elbow method per valutare l'efficacia della clusterizzazione e relativa discussione dei risultati, per capire se si può clusterizzare efficacemente su datasets con dati relativi ai problemi di transport mode detection e human activity recognition provenienti da sensori di smartphone. I risultati ottenuti sono stati confrontati con i risultati ottenuti sugli stessi dataset con l'algoritmo k-means della libreria scikit-learn, per avere un benchmark dei risultati. </li>
  <li> Valutazione delle performance dell'esecuzione dell'algoritmo k-means implementato, in termini di tempo, su diverse configurazioni di risorse, dal locale con 1 e 4 threads a cluster nel cloud con 2 e 4 nodi e relativa discussione dei dati ottenuti.
</ul>

## Dataset utilizzati
I dataset utilizzati in questo progetto sono:
<ul>
  <li><a href="http://cs.unibo.it/projects/us-tm2017/download.html">Transport mode detection</a>: questo dataset presenta 5894 samples con 64 features, con riferimento a 5 modalità di trasporto (Still, Car, Walking, Bus, Train). Il dataset in questione tra quelli nel link è il dataset bilanciato con finestra temporale di 5 secondi.
  <li><a href="https://www.kaggle.com/datasets/uciml/human-activity-recognition-with-smartphones">Human activity recognition</a>: questo dataset presenta 7353 samples con 561 features, con riferimento a 6 attività umane (walking, walking upstairs, walking downstairs, sitting, standing, laying).
</li>
</ul>

## Pre-processing dati e post processing-risultati
I due dataset prima di passare in input al K-means hanno subito una fase di pre-processazione, consistente in normalizzazione e sostituzione dei dati mancanti sulle colonne con la mediana.

I risultati dell'esecuzione del K-means sono in chunks, in quanto ogni thread produce il suo output. Per questo motivo c'è stata una fase di post processing dei risultati, che consiste nel loro parsing e conversione in csv.

Il codice relativo al pre-processing dei dati e al post-processing dei risultati è all'interno della cartella "data-processing", gli script sono stati implementati in Python usando la libreria Pandas per la manipolazione dei dataframe.

## Riassunto dei risultati

I risultati ottenuti per la clusterizzazione nei due datasets si discostano da quella che è la situazione ideale (numero di clusters individuati uguale a quello reale).

Infatti, per D1 (transport mode detection dataset) il numero dei cluster reale è 5, mentre tramite l'algoritmo k-means implementato, con tecnica dell'elbow method con wcss è stato riscontrato che è 9, mentre con indice Calinski-Harabasz è 3 e con mean wcss è 8. Per quanto riguarda il dataset D2 (human activity recognition dataset) invece, anche in questo caso si è avuta una discordanza tra i cluster reali, che sono 6, mentre quelli trovati tramite elbow method con i vari indici: 8 per wcss, 2 per Calisnki-Harabasz e 9 per la mean wcss. I risultati migliori quindi si sono ottenuti tramite l'indice wcss.
I risultati ottenuti in questo studio si discostano poco dal benchmark effettuato tramite algoritmo k-means della libreria scikit-learn. Quest'ultimo infatti tramite tecnica dell'elbow method individua il numero di clusters ottimo per D1 a 8 e 6 per D2.

I risultati ottenuti suggeriscono quindi che con elbow method tramite indice wcss è possibile fare clusterizzazione approssimativa su dati relativi alla transport mode detection e human activity recognition provenienti da sensori di smartphone. Uno sviluppo futuro possibile potrebbe essere quello di applicare preliminarmente algoritmi per eliminare quei samples nel dataset che sono frutto del rumore dei sensori, ad esempio attraverso l'algoritmo DBSCAN, per escluderli cercando così di andare a migliorare poi risultati della clusterizzazione.

Per quanto riguarda i tempi di esecuzione, questi sono stati contro le aspettative. Infatti le prestazioni migliori sono state ottenute in locale. Su questo bisogna indagare sulla potenza delle macchine nel cloud rispetto alla risorsa di calcolo locale. Ulteriori test andrebbero fatti con cluster di dimensione più grande di quella presa in considerazione in questo studio. Inoltre, una conclusione potrebbe essere quella che i dataset non sono sufficientemente grandi da poter presupporre la diminuizione dei tempi di esecuzione su un cluster rispetto ad una esecuzione su macchina locale, tenendo in considerazione che per dataset piccoli non conviene una esecuzione su cluster, in quanto l'overhead aggiunto dalla latenza di rete porta l'esecuzione locale ad essere più veloce.

## Esecuzione

Requisiti:
<ul>
  <li>Scala 2.12</li>
    <li>Spark 3.1.2</li>
  <li>Java 8 SDK </li>
  <li>Python 3.8.2</li>
</ul>

L'esecuzione del progetto si divide in diversi step:
<ol>
  <li>preparazione dei dataset</li>
  I dataset devono subire la fase di pre-processing per essere pronti per la fase di input al K-means. Eseguire quindi lo script "preprocessing.py" nella cartella "data-processing". Questo creerà due file file csv, "dataset1.csv" e "dataset2.csv" nella cartella output/preprocessing, che sarannò i file che può prendere in input il K-means (uno alla volta).
  <li>preparazione del file .jar</li>
  l'implementazione in scala del K-means deve essere impacchattata in un file .jar, questo lo si fa digitando il comando "package" dalla shell di scala build tools, posizionandosi sulla cartella del progetto. Questo comando creerà un file .jar nella cartella target/scala-2.12. A questo punto il programma è pronto per essere eseguito
  <li>esecuzione del file .jar</li> l'esecuzione del programma avviene lanciando l'esecuzione java con input i seguenti parametri:
  <ul>
    <li>path del file .jar</li>
    <li> path cartella di input del dataset</li>
    <li>path cartella di output del dataset</li>
    <li>numero threads</li> Settare "*" per numero massimo di threads messi a disposizione dalla macchina, sennò un numero a propria scelta
    <li>numero di clusters</li> Numero di cluster per cui si vuole fare lo studio dell'elbow method. Ad esempio con 5 il k-means verrà eseguito per 5 volte con numero di clusters da 2,3,4,5.
    <li>epsilon</li> coefficiente di soglia variazione dei cluster
    <li>classe entry points</li> In questo caso "Main"
  </ul>
  </ol>
  
L'ultimo punto è equivalente sia per l'esecuzione in locale che in cloud. Nel caso del cloud, i path dovranno essere riferiti al file system dello storage del servizio cloud in questione (in questo caso Google Cloud Storage).

Per l'esecuzione sul cloud occorre:
<ol>
  <li>
    creare quindi un bucket che contenga i file di input e di output e il file jar
  </li>
    <li>
    creare i cluster su cui si desidera testare l'esecuzione settandoli con numero arbitrario di master e nodi
  </li>
      <li>
     crere il job dove si specifica cluster da usare, path del jar da eseguire, class entry point, input da terminale
  </li>
  <li>
    Avviare l'esecuzione del cluster e del job e aspettare la fine dell'esecuzione, dopo la quale si può arrestare l'esecuzione del cluster e del job.
  </li>
</ol>

