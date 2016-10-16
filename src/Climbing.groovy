class Climbing {
    /*
     ArrayList entfernungstabelle = [[0, 3, 5, 1, 7],
                                     [3, 0, 7, 2, 6],
                                     [5, 7, 0, 3, 5],
                                     [1, 2, 3, 0, 4],
                                     [7, 6, 5, 4, 0]];
                                     */
    final int ANZAHLSTAEDTE = 100
    final int ANZAHL_DURCHLAEUFE = 100000
    final Random RANDOM = new Random()
    final ArrayList ENTFERNUNGSTABELLE = fillTable() // tabelle mit zufallswerten symmetrisch füllen

    public static void main(String[] args) {
        // Simulated Annealing im Schnitt besser: 175 (5000000 Durchläufe, Entfernung 0-49 km)
        // Simulated Annealing im Schnitt besser: 143.066(1000000 Durchläufe, Entfernung 0-49 km)
        // Simulated Annealing im Schnitt besser: 91.455 (100000 Durchläufe, Entfernung 0-49 km)
        // Simulated Annealing im Schnitt besser: 58.9   (50000 Durchläufe, Entfernung 0-49 km)
        // Simulated Annealing im Schnitt besser: 14.305 (10000 Durchläufe, Entfernung 0-49 km)
        // Simulated Annealing im Schnitt besser: -3.194 (1000 Durchläufe, Entfernung 0-49 km)
        // Simulated Annealing im Schnitt besser: -0.952 (100 Durchläufe, Entfernung 0-49 km)


        int anzahl = 1
        int sum = 0

        anzahl.times {
            Climbing climbing = new Climbing()
            int hc = climbing.hillClimbing()
            int sa = climbing.simulatedAnnealing()
            println "HillClimbing: $hc\tSimulatedAnnealing: $sa"
            sum += (sa - hc)
        }

        println "Simulated Annealing im Schnitt besser: ${sum / anzahl}"
    }

    int hillClimbing() {
        Integer threshold = -300 // erwartete fitness
        ArrayList<Integer> hypothesis = fillHypothesis()
        // ist eine mögliche rundreise, start ist die einfachste 0,1,2,3,4
        ArrayList<Integer> savestate // eine zwischengespeicherte hypothesis
        Integer lastFitness = calculateFitness(hypothesis)

        ANZAHL_DURCHLAEUFE.times {
            savestate = hypothesis.clone()
            hypothesis = moveOneStepAtRandom(hypothesis) // 2 Städte vertauschen
            Integer currentFitness = calculateFitness(hypothesis)

            println "CurrentFitness: $currentFitness \t LastFitness: $lastFitness"

            if (currentFitness > lastFitness) {
                lastFitness = currentFitness
            } else {
                hypothesis = savestate
            }

            println("New Fitness: $lastFitness")
            println "run ${it + 1}\n"
            //sleep(50)
        }

        return lastFitness;
    }

    int simulatedAnnealing() {
        Integer threshold = -300 // erwartete fitness
        ArrayList<Integer> hypothesis = fillHypothesis()
        // ist eine mögliche rundreise, start ist die einfachste 0,1,2,3,4
        ArrayList<Integer> savestate // eine zwischengespeicherte hypothesis
        Integer lastFitness = calculateFitness(hypothesis)
        double temperature = 5.0
        double epsilon = temperature / ANZAHL_DURCHLAEUFE
        int i = 1

        while (temperature > epsilon) {
            savestate = hypothesis.clone()
            hypothesis = moveOneStepAtRandom(hypothesis) // 2 Städte vertauschen
            Integer currentFitness = calculateFitness(hypothesis)

            println "CurrentFitness: $currentFitness \t LastFitness: $lastFitness"

            if (currentFitness <= lastFitness) {
                def ran = Math.random()
                def wahrsch = Math.exp(((currentFitness - lastFitness) / temperature))
                println "$currentFitness - $lastFitness =${currentFitness - lastFitness}"
                print "$ran < $wahrsch = "
                println ran < wahrsch
            }
            if (currentFitness > lastFitness) {
                lastFitness = currentFitness
            } else {
                if (Math.random() < Math.exp(((currentFitness - lastFitness) / temperature))) {
                    lastFitness = currentFitness
                } else {
                    hypothesis = savestate
                }

            }

            temperature -= epsilon

            println("New Fitness: $lastFitness \t Temperature: $temperature")
            println "run ${i++}\n"

            //sleep(50)
        }

        return lastFitness
    }

    /**
     * Berechnet die Fitness der übergebenen Rundreise
     * @param hypothesis
     * @return die berechnete Fitnesszahl
     */
    int calculateFitness(ArrayList<Integer> hypothesis) {
        int fitness = 0

        ANZAHLSTAEDTE.times {
            Integer abreisestadt = hypothesis[it]
            Integer ankunftstadt = hypothesis[(it + 1) % 100]
            fitness += ENTFERNUNGSTABELLE[abreisestadt][ankunftstadt]

            //print "${entfernungstabelle[abreisestadt][ankunftstadt]} "
        }

        return fitness * -1
    }

    /**
     * Tauscht 2 Städte der Hypothesis zufällig
     * @param hypothesis
     * @return die vertausche hypothesis
     */
    ArrayList<Integer> moveOneStepAtRandom(ArrayList<Integer> hypothesis) {
        Integer tauschpartner1 = RANDOM.nextInt(ANZAHLSTAEDTE)
        Integer tauschpartner2 = RANDOM.nextInt(ANZAHLSTAEDTE)

        while (tauschpartner1 == tauschpartner2) {
            tauschpartner2 = RANDOM.nextInt(ANZAHLSTAEDTE)
        }

        Integer temp = hypothesis[tauschpartner1]

        hypothesis[tauschpartner1] = hypothesis[tauschpartner2]
        hypothesis[tauschpartner2] = temp

        return hypothesis
    }

    /**
     * Definiert die Entfernungstabelle symmetrisch und zufällig
     * @return die Entfernungtabelle
     */
    ArrayList<ArrayList<Integer>> fillTable() {
        ArrayList res = new ArrayList()
        ANZAHLSTAEDTE.times {
            res[it] = new ArrayList<>()
        }
        for (int i = 0; i < ANZAHLSTAEDTE; i++) {
            for (int j = 0; j < ANZAHLSTAEDTE; j++) {
                if (i == j) { // von Stadt A nach A = 0km
                    res[i][j] = 0
                } else if (res[i][j] == null) {
                    Integer val = RANDOM.nextInt(50) // 0 bis 49 km zwischen den Städten
                    res[i][j] = val // von Stadt A nach B = X km UND
                    res[j][i] = val // von Stadt B nach A = X km
                }
            }
        }
        return res
    }

    /**
     * Definiert die Starthypothesis fortlaufend
     * @return die Starthypothesis
     */
    ArrayList<Integer> fillHypothesis() {
        def res = new ArrayList()
        ANZAHLSTAEDTE.times {
            res[it] = it
        }
        return res
    }
}