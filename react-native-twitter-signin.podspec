require 'json'

package = JSON.parse(File.read(File.join(__dir__, 'package.json')))

Pod::Spec.new do |s|
  s.name        = 'react-native-twitter-signin'
  s.version     = package['version']
  s.summary     = package['description']
  s.homepage    = package['homepage']
  s.license     = package['license']
  s.author      = 'Justin Nguyen'
  s.platform    = :ios, "9.0"
  s.source      = { :git => "https://github.com/react-native-twitter-signin/twitter-signin.git", :tag => "#{s.version}" }

  s.source_files  = "ios/*.{h,m,mm}"

  s.dependency "React"
  s.dependency "TwitterKit5"
end
