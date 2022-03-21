# Introduzione

L'obiettivo di questo progetto è studiare è studiare l'efficacia dell'algoritmo K-means per problemi di transport mode detection e human activity recognition, su dataset fatti da dati provenienti da sensori di smartphone.

Lo studio è stato svolto adottando tecniche di analisi dei dati scalabili, tramite Spark, una libreria che permette di eseguire computazione parallela scrivendo codice sequenziale, con relativa gestione delle risorse di calcolo. Infatti lo stesso codice è stato mandado in esecuzione su diverse risorse di calcolo, dalla macchina locale ai cluster con più nodi di calcolo del servizio di cloud computing Google Cloud Platform.

Il linguaggio di programmazione utilizzato è Scala, un linguaggio tipato staticamente la cui compilazione del codice produce Java Bytecode eseguibile sulla Java Virtual Machine. Supporta la programmazione object oriented e la programmazione funzionale.

In questo progetto è stato implementato l'algoritmo di clusterizzazione K-means per dataset con numero arbitrario di features.

## Punti chiave del progetto
<ul>
  <li> Implementazione algoritmo K-means per dataset con n-features</li>
  <li> Implementazione algoritmo per il calcolo della WCSS, che è una metrica per valutare la qualità di un cluster</li>
  <li> Raccolta dati per valutare l'efficacia della clusterizzazione tramite elbow method </li>
  <li> Valutazione delle performance dell'esecuzione del codice in termini di tempo su diverse configurazioni di risorse, dal locale a cluster nel cloud con cluster con 1,2,4 nodi.
</ul>

## Riassunto dei risultati
 

### Esecuzione

Requisiti:
<ul>
  <li>Scala 2.12</li>
    <li>Spark 3.1.2</li>
</ul>


