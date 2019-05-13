(ns.file-loader-server
  (require '[file-loader.sorters :as sorters]
           '[file-loader.personHelpers :as helpers])
  (use '[org.httpkit.server]
       '[compojure.core :only [defroutes GET POST DELETE ANY context]]
       '[ring.util.json-response]))

(defn sortByDob []
  (let [data (helpers/get-all-data)
        normalizedData (helpers/normalizePeopleData data)]
  (json-response (sorters/sortByDob normalizedData))))

(defn sortByGender []
  (let [data (helpers/get-all-data)
        normalizedData (helpers/normalizePeopleData data)]
  (json-response (sorters/sortByGenderLastName normalizedData))))

(defn sortByName []
  (let [data (helpers/get-all-data)
        normalizedData (helpers/normalizePeopleData data)]
  (json-response (sorters/sortByLastName normalizedData))))

(defroutes app
  (GET "/records/birthdate" [] (sortByDob))
  (GET "/records/gender" [] (sortByGender))
  (GET "/records/name" [] (sortByName)))

(defonce server (atom nil))

(reset! server (run-server #'app {:port 8080}))

(defn stop-server []
  (when-not (nil? @server)
    ;; graceful shutdown: wait 100ms for existing requests to be finished
    ;; :timeout is optional, when no timeout, stop immediately
    (@server :timeout 100)
    (reset! server nil)))