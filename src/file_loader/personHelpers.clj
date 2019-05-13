(ns file-loader.personHelpers
  (require [clojure.string :as str]))

; Assumes that the order of each file is the same
(defonce personMapDef [
  :lastName
  :firstName
  :gender
  :favColor
  :dob
  ])

(defonce fields_per_person (count personMapDef))

(defn get-all-data []
  "Grabs all three text files and joins them together"
  (concat
    (str/split (slurp "src/file_loader/pipesep.txt") #"\|")
    (str/split (slurp "src/file_loader/commasep.txt") #"\,")
    (str/split (slurp "src/file_loader/spacesep.txt") #" ")))

(defn generatePersonMap [values]
  "Takes in a vector of people and converts them to a map based on `personMapDef`"
  (loop [i 0
        personMap {}]
    (if (= i fields_per_person)
      personMap
      (recur (inc i) (conj personMap {
        (get personMapDef i)
        (nth values i)})
        ))))

(defn normalizePeopleData [data]
  "Takes the people data and turns it into a consistent map"
  (loop [i 0
         normalizedPeople []
         data data]
    (if (= 0 (count data))
      normalizedPeople
      (do ;TODO remove this was there for testing
        (recur (+ 5 i)
          (conj normalizedPeople (generatePersonMap (take fields_per_person data)))
          (drop fields_per_person data))))))