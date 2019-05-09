(ns file-loader.core
  (require '[clojure.string :as str])
  (use 'java-time))

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

(def format-date-for-sort (comp (partial local-date "M/d/yyyy") :dob))

(defn sortByGenderLastName [data]
  "sorts by gender F - > M using fact that F is first"
  (sort-by (juxt :gender :lastName) data))

(defn sortByDob [data]
  "sorts DOB ascending"
  (sort-by format-date-for-sort data))

(defn sortByLastName [data]
  "sort by last name decending z -> a"
  (reverse (sort-by :lastName data)))



(let [fileContents (get-all-data)]
  (loop [i 0
         normalizedPeople []
         fileContents fileContents]
    (if (= 0 (count fileContents))
      (do
        (println sortByGenderLastName normalizedPeople)
        (println sortByDob normalizedPeople)
        (println sortByLastName normalizedPeople)
        nil) ; explicit
      (do ;TODO remove this was there for testing
        (recur (+ 5 i)
          (conj normalizedPeople (generatePersonMap (take fields_per_person fileContents)))
          (drop fields_per_person fileContents))))))
