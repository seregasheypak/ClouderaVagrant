require 'puppet/provider/asadmin'
Puppet::Type.type(:jvmoption).provide(:asadmin, :parent =>
                                           Puppet::Provider::Asadmin) do
  desc "Glassfish jvm-options support."
  commands :asadmin => "asadmin"

  def doCreate(optionName)
    self.debug "creating " + optionName
    args = []
    args << "create-jvm-options"
    args << "'" + optionName + "'"
    asadmin_exec(args)
  end

  def doRemove(optionName)
    self.debug "removing " + optionName
    args = []
    args << "delete-jvm-options" << "'" + optionName + "'"
    asadmin_exec(args)
  end

  def create
    multiple = findSameOptions
    multiple.each do |mult|
      doRemove(escape(mult))
    end

    doCreate(escape(@resource[:name]))
#    asadmin_exec(["restart-domain"])
  end

  def destroy
    doRemove(escape(@resource[:name]))
#    asadmin_exec(["restart-domain"])
  end

  def exists?
    multiple = findSameOptions
    self.debug "multiples: " + multiple.to_s 
    if multiple.size != 1
      return false
    elsif multiple[0] != @resource[:name]
      return false
    end
    return true
  end

  def nameAndValue(fullOption)
    indexOfEqual = fullOption.index('=')
	return fullOption[0..(indexOfEqual-1)], fullOption[(indexOfEqual+1)..fullOption.length]
  end

  def findSameOptions   
    result = [] 
    unaryOptionsPrefixes = ["-Xmx", "-Xms"]
    fullOption = @resource[:name]
    indexOfEqual = fullOption.index('=')

    allOptions = asadmin_exec(["list-jvm-options"])
    self.debug "allOptions: " + allOptions.to_s

    if indexOfEqual != nil
      optionName, optionValue = nameAndValue(fullOption)
      self.debug "optionName " + optionName + " optionValue '" + optionValue + "'"
      allOptions.each do |line|
        line.sub!(/-XX: ([^\ ]+)/, '-XX:+\1')
        line = line.chomp
        if line.index('=') != nil
          prName, prValue = nameAndValue(line)
          if prName == optionName
            result.push(line)
          end
        end
      end
    else
      self.debug "fullOption " + fullOption
      unaryOptionsPrefixes.each do |el|
        if(fullOption.start_with?(el))
          allOptions.each do |line|
            line.sub!(/-XX: ([^\ ]+)/, '-XX:+\1')
            line = line.chomp
            if line.index('=') == nil and line.start_with?(el)
              result.push(line)
            end
          end
        end
      end
    end
    return result
  end
end
