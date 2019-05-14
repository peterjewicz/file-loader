(ns file-loader.server
  (:require [file-loader.sorters :as sorters]
            [file-loader.personHelpers :as helpers]
            [clojure.string :as str])
  (:use [org.httpkit.server]
        [compojure.core :only [defroutes GET POST context]]
        [ring.util.json-response]
        [ring.middleware.params]))

(defonce server (atom nil))

(defn formatResponse [req]
  (if (str/includes? req "|")
    (str/split req #"\|")
    (if (str/includes? req " ")
      (str/split req #" ")
      (if (str/includes? req ",")
        (str/split req #",")
        (println "invalid file format"))))) ; TODO real app error handling

; Route handlers defined below
(defn addRecord [req]
  (let [value (-> req :params (get "value"))]
    (if (= 5 (count (formatResponse value)))
      (json-response value) ; In a real application we could add some persistence here
      (println "Too Few Fields")))) ; TODO real app error handling

(defn sortByDob []
  (let [data (helpers/getAllData)
        normalizedData (helpers/normalizePeopleData data)]
  (json-response (sorters/sortByDob normalizedData))))

(defn sortByGender []
  (let [data (helpers/getAllData)
        normalizedData (helpers/normalizePeopleData data)]
  (json-response (sorters/sortByGenderLastName normalizedData))))

(defn sortByName []
  (let [data (helpers/getAllData)
        normalizedData (helpers/normalizePeopleData data)]
  (json-response (sorters/sortByLastName normalizedData))))

(defroutes appRoutes
  (POST "/records" [] addRecord)
  (GET "/records/birthdate" [] (sortByDob))
  (GET "/records/gender" [] (sortByGender))
  (GET "/records/name" [] (sortByName)))

(def app ; this is required or you won't get params in post request...
  (-> appRoutes
    ring.middleware.params/wrap-params))

(defn start-server []
  (reset! server (run-server #'app {:port 8080})))

(defn stop-server []
  (when-not (nil? @server)
    ;; graceful shutdown: wait 100ms for existing requests to be finished
    ;; :timeout is optional, when no timeout, stop immediately
    (@server :timeout 100)
    (reset! server nil)))