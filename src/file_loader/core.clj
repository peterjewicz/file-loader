(ns file-loader.core
  (require
           '[file-loader.sorters :as sorters]
           '[file-loader.personHelpers :as helpers]))

(defn -main []
  (let [fileContents (helpers/get-all-data)
        normalizedPeople (helpers/normalizePeopleData fileContents)]
        (println (sorters/sortByGenderLastName normalizedPeople))
        (println (sorters/sortByDob normalizedPeople))
        (println (sorters/sortByLastName normalizedPeople))))
