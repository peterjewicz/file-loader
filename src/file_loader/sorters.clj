(ns file-loader.sorters
  (use java-time))

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