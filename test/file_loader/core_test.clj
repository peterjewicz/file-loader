(ns file-loader.core-test
  (:require [clojure.test :refer :all]
            [file-loader.sorters :as sorters]
            [file-loader.personHelpers :as helpers]
            [org.httpkit.client :as http]
            [file-loader.server :refer [start-server stop-server]]))

; Mount our server for the tests
(defn start-states [f]
  (start-server)
  (f)
  (stop-server))

(clojure.test/use-fixtures :once start-states)
; Setup some hardcoded values below to make tests independant of the file data
(def dummyMapData [
  {:lastName "z" :firstName "a" :favColor "red" :gender "M" :dob "11/6/1995"}
  {:lastName "a" :firstName "z" :favColor "blue" :gender "F" :dob "11/6/1990"}
  {:lastName "m" :firstName "m" :favColor "yellow" :gender "M" :dob "11/6/2016"}
  {:lastName "r" :firstName "b" :favColor "orange" :gender "F" :dob "11/6/1965"}])

; Example data after calling 'slup'
(def dummyUNmappedData ["z" "a" "M" "red" "1/9/94"])

; Test our generation functions in a real application we'd probably have a separate test file for these
(deftest generateMap
  (testing "generate our map from a vector of people values"
    (is (= (helpers/generatePersonMap dummyUNmappedData)
        {:lastName "z" :firstName "a" :gender "M" :favColor "red" :dob "1/9/94"}))))

; Test our 3 sorts
(deftest sortByDob
  (testing "sort by dob"
    (is (= (into [] (sorters/sortByDob dummyMapData))
        [{:lastName "r" :firstName "b" :favColor "orange" :gender "F" :dob "11/6/1965"}
          {:lastName "a" :firstName "z" :favColor "blue" :gender "F" :dob "11/6/1990"}
          {:lastName "z" :firstName "a" :favColor "red" :gender "M" :dob "11/6/1995"}
          {:lastName "m" :firstName "m" :favColor "yellow" :gender "M" :dob "11/6/2016"}]))))

(deftest sortByLastName
  (testing "sort by last name ascending"
    (is (= (into [] (sorters/sortByLastName dummyMapData))
      [{:lastName "z" :firstName "a" :favColor "red" :gender "M" :dob "11/6/1995"}
        {:lastName "r" :firstName "b" :favColor "orange" :gender "F" :dob "11/6/1965"}
        {:lastName "m" :firstName "m" :favColor "yellow" :gender "M" :dob "11/6/2016"}
        {:lastName "a" :firstName "z" :favColor "blue" :gender "F" :dob "11/6/1990"}]))))

(deftest sortByGenderThenLastName
  (testing "sorts gender then last name"
    (is (= (into [] (sorters/sortByGenderLastName dummyMapData))
      [{:lastName "a" :firstName "z" :favColor "blue" :gender "F" :dob "11/6/1990"}
        {:lastName "r" :firstName "b" :favColor "orange" :gender "F" :dob "11/6/1965"}
        {:lastName "m" :firstName "m" :favColor "yellow" :gender "M" :dob "11/6/2016"}
        {:lastName "z" :firstName "a" :favColor "red" :gender "M" :dob "11/6/1995"}]))))


; Server tests - bound to the input files
; indirectly also tests our parsing file flow so more integration than unit
(deftest server-dob
  (testing "test /records/birthday"
    (is (=  (let [{:keys [status headers body error] :as resp} @(http/get "http://localhost:8080/records/name")] body)
      "[{\"lastName\":\"Yessir\",\"firstName\":\"Boris\",\"gender\":\"M\",\"favColor\":\"silver\",\"dob\":\"7/11/1946\"},{\"lastName\":\"Smith\",\"firstName\":\"Jane\",\"gender\":\"F\",\"favColor\":\"yellow\",\"dob\":\"8/17/1979\"},{\"lastName\":\"Smith\",\"firstName\":\"Jimmy\",\"gender\":\"M\",\"favColor\":\"yellow\",\"dob\":\"11/12/1976\"},{\"lastName\":\"Schmitt\",\"firstName\":\"Kaley\",\"gender\":\"F\",\"favColor\":\"red\",\"dob\":\"1/28/1995\"},{\"lastName\":\"Ryans\",\"firstName\":\"Jagaur\",\"gender\":\"M\",\"favColor\":\"purple\",\"dob\":\"4/19/2006\"},{\"lastName\":\"Jones\",\"firstName\":\"Bob\",\"gender\":\"M\",\"favColor\":\"orange\",\"dob\":\"5/6/1985\"},{\"lastName\":\"Jewicz\",\"firstName\":\"Peter\",\"gender\":\"M\",\"favColor\":\"blue\",\"dob\":\"1/9/1994\"}]"
    ))))

(deftest server-gender
  (testing "test /records/gender"
    (is (=  (let [{:keys [status headers body error] :as resp} @(http/get "http://localhost:8080/records/gender")] body)
    "[{\"lastName\":\"Schmitt\",\"firstName\":\"Kaley\",\"gender\":\"F\",\"favColor\":\"red\",\"dob\":\"1/28/1995\"},{\"lastName\":\"Smith\",\"firstName\":\"Jane\",\"gender\":\"F\",\"favColor\":\"yellow\",\"dob\":\"8/17/1979\"},{\"lastName\":\"Jewicz\",\"firstName\":\"Peter\",\"gender\":\"M\",\"favColor\":\"blue\",\"dob\":\"1/9/1994\"},{\"lastName\":\"Jones\",\"firstName\":\"Bob\",\"gender\":\"M\",\"favColor\":\"orange\",\"dob\":\"5/6/1985\"},{\"lastName\":\"Ryans\",\"firstName\":\"Jagaur\",\"gender\":\"M\",\"favColor\":\"purple\",\"dob\":\"4/19/2006\"},{\"lastName\":\"Smith\",\"firstName\":\"Jimmy\",\"gender\":\"M\",\"favColor\":\"yellow\",\"dob\":\"11/12/1976\"},{\"lastName\":\"Yessir\",\"firstName\":\"Boris\",\"gender\":\"M\",\"favColor\":\"silver\",\"dob\":\"7/11/1946\"}]"
    ))))

(deftest server-name
  (testing "test /records/name"
    (is (=  (let [{:keys [status headers body error] :as resp} @(http/get "http://localhost:8080/records/name")] body)
      "[{\"lastName\":\"Yessir\",\"firstName\":\"Boris\",\"gender\":\"M\",\"favColor\":\"silver\",\"dob\":\"7/11/1946\"},{\"lastName\":\"Smith\",\"firstName\":\"Jane\",\"gender\":\"F\",\"favColor\":\"yellow\",\"dob\":\"8/17/1979\"},{\"lastName\":\"Smith\",\"firstName\":\"Jimmy\",\"gender\":\"M\",\"favColor\":\"yellow\",\"dob\":\"11/12/1976\"},{\"lastName\":\"Schmitt\",\"firstName\":\"Kaley\",\"gender\":\"F\",\"favColor\":\"red\",\"dob\":\"1/28/1995\"},{\"lastName\":\"Ryans\",\"firstName\":\"Jagaur\",\"gender\":\"M\",\"favColor\":\"purple\",\"dob\":\"4/19/2006\"},{\"lastName\":\"Jones\",\"firstName\":\"Bob\",\"gender\":\"M\",\"favColor\":\"orange\",\"dob\":\"5/6/1985\"},{\"lastName\":\"Jewicz\",\"firstName\":\"Peter\",\"gender\":\"M\",\"favColor\":\"blue\",\"dob\":\"1/9/1994\"}]"
    ))))
