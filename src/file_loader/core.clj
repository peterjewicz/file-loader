(ns file-loader.core
  (require '[clojure.string :as str]))
; probably define a record here would be better
(defonce personMapDef [ ; Assumes that the order of each file is the same
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
        )))
  )


(defn sortByGender [data]
  "sorts by gender F - > M using fact that F is first"
  ; (println data)
  ; (into [] (sort-by :gender data)))
  (sort-by (juxt :gender :favColor) data))


(let [fileContents (get-all-data)]
  (loop [i 0
         normalizedPeople []
         fileContents fileContents]
    (if (= 0 (count fileContents))
      (sort-by-dob normalizedPeople)
      (do ;TODO remove this was there for testing
        (recur (+ 5 i)
          (conj normalizedPeople (generatePersonMap (take fields_per_person fileContents)))
          (drop fields_per_person fileContents))))))
