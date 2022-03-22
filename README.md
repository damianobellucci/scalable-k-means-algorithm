# Scalable K-means algorithm 

## Introduzione
L'obiettivo di questo progetto è studiare è studiare l'efficacia dell'algoritmo K-means implementato per essere eseguito su risorse scalabili, per problemi di transport mode detection e human activity recognition, su dataset fatti da dati provenienti da sensori di smartphone. 
In particolare, si è cercato di capire se a partire da questi dati è possibile fare clusterizzazione per individuare i cluster di dati relativi ai tipi di trasporto oppure alle attività umane.
Possibili risvolti di questo studio possono essere il riconoscimento di tipi diversi modalità di trasporto/attività umane a partire da dati di sensori, senza però conoscerne la denominazione. 

Lo studio è stato svolto adottando tecniche di analisi dei dati scalabili, tramite Spark, una libreria che permette di eseguire computazione parallela scrivendo codice sequenziale, con relativa gestione delle risorse di calcolo. Infatti lo stesso codice è stato mandado in esecuzione su diverse risorse di calcolo, dalla macchina locale ai cluster con più nodi di calcolo del servizio di cloud computing Google Cloud Platform. 
L'esecuzione su risorse scalabili permette di capire se lo studio può essere effettuato, anche in futuro con diversi dataset, più efficacamente avendo a disposizione più capacità di calcolo.

Il linguaggio di programmazione utilizzato è Scala, un linguaggio tipato staticamente la cui compilazione del codice produce Java Bytecode eseguibile sulla Java Virtual Machine. Supporta la programmazione object oriented e la programmazione funzionale.


## Punti chiave del progetto
<ul>
  <li> Implementazione algoritmo K-means per dataset con n-features</li>
  <li> Implementazione algoritmo per il calcolo della WCSS, che è una metrica per valutare la qualità di un cluster</li>
  <li> Raccolta dati per valutare l'efficacia della clusterizzazione tramite elbow method </li>
  <li> Valutazione delle performance dell'esecuzione del codice in termini di tempo su diverse configurazioni di risorse, dal locale a cluster nel cloud con cluster con 1,2,4 nodi.
</ul>

## Dataset utilizzati
I dataset utilizzati in questo progetto sono:
<ul>
  <li><a href="google.it">Transport mode detection</a>: questo dataset presenta circa 6000 samples con 64 features, con riferimento a 5 modalità di trasporto (Still, Car, Walking, Bus, Train).
  <li><a href="https://www.kaggle.com/datasets/uciml/human-activity-recognition-with-smartphones">Human activity recognition</a>: questo dataset presenta samples con 561 features, con riferimento a 6 attività umane (walking, walking upstairs, walking downstairs, sitting, standing, laying).
</li>
</ul>

## Pre-processing dati e post processing risultati
I due dataset prima di passare in input al K-means hanno subito una fase di pre-processazione, consistente in normalizzazione e sostituzione dei dati mancanti sulle colonne con la mediana.

I risultati dell'esecuzione del K-means sono in chunks, in quanto ogni thread produce il suo output. Per questo motivo c'è stata una fase di post processing dei risultati, che consiste nel loro parsing e conversione in csv.

Il codice relativo al pre-processing dei dati e al post-processing dei risultati è all'interno della cartella "data-processing", gli script sono stati implementati in Python usando la libreria Pandas per la manipolazione dei dataframe.

## Riassunto dei risultati



## Esecuzione

Requisiti:
<ul>
  <li>Scala 2.12</li>
    <li>Spark 3.1.2</li>
</ul>


